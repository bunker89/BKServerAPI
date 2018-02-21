package com.bunker.bkframework.server.async;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class AsyncDeamon implements Runnable {
   private final int QUEUE_SIZE = 100;
   private final List<AsyncRun> overflowQueue = new LinkedList<>();
   private final AsyncRun []mPushQueue = new AsyncRun[QUEUE_SIZE];
   private static final AsyncDeamon This = new AsyncDeamon();
   private int front = 0, rear = 0;
   private boolean isRunning = false;
   private boolean isOverFlowed = false;
   private final Object mRunMutex = new Object();
   private long mReserveMinTime = 0;

   private AsyncDeamon() {
   }

   public static AsyncDeamon getInstance() {
      return This;
   }

   @Override
   public void run() {
      synchronized (mRunMutex) {
         if (isRunning)
            return;
         isRunning = true;
      }

      while (true) {
         while (front != rear) {
            AsyncRun run;
            int index = rear;
            run = mPushQueue[index];
            if (++rear == QUEUE_SIZE)
               rear = 0;
            try {
               run.run();
            } catch (Exception e) {
               e.printStackTrace();
               isRunning = false;
               restore(run);
               return;
            }
         }

         if (isOverFlowed) {
            overFlowHandle(null);
            continue;
         }

         long timeMillisec = Calendar.getInstance().getTimeInMillis(); 
         if (timeMillisec < mReserveMinTime) {
            try {
               Thread.sleep(100);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            if (mReserveMinTime - timeMillisec > 2000)
               break;
            continue;
         }

         break;
      }

      isRunning = false;
   }

   private void restore(AsyncRun run) {
      startDeamon();
   }

   private void startDeamon() {
      new Thread(this).start();
   }

   public void addPushUsingPool() {
   }

   synchronized public void addPush(AsyncRun push) {
      if (!isOverFlowed) {
         synchronized (mRunMutex) {
            if (rear != ((front + 1) % QUEUE_SIZE)) {
               mPushQueue[front] = push;
               if (++front == QUEUE_SIZE)
                  front = 0;
            } else
               isOverFlowed = true;
         }
      }

      if (isOverFlowed) {
         overFlowHandle(push);
      }

      if (!isRunning)
         startDeamon();
   }

   private void overFlowHandle(AsyncRun push) {
      synchronized (overflowQueue) {
         if (push != null)
            overflowQueue.add(push);

         synchronized (mRunMutex) {
            while (rear != (front + 1) % QUEUE_SIZE && overflowQueue.size() > 0) {
               mPushQueue[front] = overflowQueue.remove(0);
               if (++front == QUEUE_SIZE)
                  front = 0;
            }
         }

         if (overflowQueue.size() == 0) {
            isOverFlowed = false;
         }
      }
   }

   public void reserveMinOneSecond() {
      mReserveMinTime = Calendar.getInstance().getTimeInMillis() + 2000;
   }

   public boolean isRunning() {
      return isRunning;
   }

   static int i = 0;
   public static void main(String args[]) {
      AsyncDeamon t = new AsyncDeamon();
      t.reserveMinOneSecond();
      AsyncRun runnable = new AsyncRun() {

         @Override
         synchronized public void run() {
            i /= 2;
         }

         @Override
         public String err() {
            return null;
         }
      };

      for (int i = 0; i < 1000000; i++) {
         t.addPush(runnable);
      }
   }
}