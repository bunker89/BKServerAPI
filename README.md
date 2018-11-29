# BKServerAPI

BKServer is similar with microservice architecture.
It is separated into small, independent services named 'Working'.
Every service in the 'Working' class must be linked to use parameter of JSON.
------------------------------- helped by andrei----------------------------------
For example Calculator Server can has 'PlusWorking', 'MinusWorking', 'MultiWorking', 'DivideWorking'.
'WorkContainer' could be consist of many coordination.
It can be reduce the association.
'Working' don't have dependency to Java. 'Working' can bridge to every laungage of can using JSON.
'PlusWorking' can be made by 'Java' and 'MinusWorking' can be made by 'C'.
Even though ther are developded several laungage, they can be linked very simply.
