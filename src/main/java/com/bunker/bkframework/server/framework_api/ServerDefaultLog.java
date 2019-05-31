package com.bunker.bkframework.server.framework_api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.bunker.bkframework.newframework.Log;
import com.bunker.bkframework.newframework.Logger;

public class ServerDefaultLog implements Log {
	private final String ERROR_FOLDER = "error_info/";
	private FileWriter mErrorOutput;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy:MM:dd HH:mm:ss]");
	
	public ServerDefaultLog() {
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		File file = new File("error_info");
		file.mkdir();
		
		file = new File("error");
		file.delete();
		try {
			file.createNewFile();
			mErrorOutput = new FileWriter(file);
			
			File out = new File("log");
			System.setOut(new PrintStream(out));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File createErrorFile(String tag, long errorId) {
		try {
			mErrorOutput.write(errorId + " " + dateFormat.format(new Date()) + " " + tag + "\n");
			mErrorOutput.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		File file = new File(ERROR_FOLDER + errorId);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	@Override
	public long err(String tag, String text) {
		long id = Calendar.getInstance().getTimeInMillis();
		File file = createErrorFile(tag, id);

		FileOutputStream output;
		try {
			output = new FileOutputStream(file);
			PrintWriter writer = new PrintWriter(output);
			writer.write(text + "\n");
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		return id;
	}

	@Override
	public long err(String tag, String text, Exception e) {
		long id = Calendar.getInstance().getTimeInMillis();
		
		File file = createErrorFile(tag, id);
		FileOutputStream output;
		try {
			output = new FileOutputStream(file);
			PrintWriter writer = new PrintWriter(output);
			writer.write(text + "\n\n "
					+ "*****stack trace*****\n" + e.getMessage() + "\n");
			e.printStackTrace(writer);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return id;
	}

	@Override
	public void warning(String title, String text) {
		String tag = dateFormat.format(new Date()) + ":" + title + " : " + text;
		System.out.println("*** warning -> " + tag + "***");
	}

	@Override
	public void log(String title, String text) {
		String tag = dateFormat.format(new Date()) + ":" + title + " : " + text;
		System.out.println(tag);
	}

	public static void main(String []args) {
		Log log = new ServerDefaultLog();
		try {
			FileInputStream stream = new FileInputStream("asbass");
		} catch (FileNotFoundException e) {
			Logger.err("tag", "test", e);
		}
	}
}