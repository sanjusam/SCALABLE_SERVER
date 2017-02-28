all: clean compile
	@echo -e '[INFO] Done!'

clean:
	@echo -e '[INFO] Cleanup class files'
	@rm  -rf ./cs455/scaling/task/Task.class
	@rm  -rf ./cs455/scaling/task/TaskType.class
	@rm  -rf ./cs455/scaling/task/VoidTask.class
	@rm  -rf ./cs455/scaling/task/WriteTask.class
	@rm  -rf ./cs455/scaling/threadpool/WorkerThread.class
	@rm  -rf ./cs455/scaling/task/ReadAndCalculateHash.class
	@rm  -rf ./cs455/scaling/taskQueue/TaskQueueManager.class
	@rm  -rf ./cs455/scaling/threadpool/ThreadPoolManager.class
	@rm  -rf ./cs455/scaling/TaskOrchestrator/TaskDispatcherThread.class
	@rm  -rf ./cs455/scaling/utils/ValidateCommandLine.class
	@rm  -rf ./cs455/scaling/server/ConnectionListenerThread.class
	@rm  -rf ./cs455/scaling/server/Server.class
	@rm  -rf ./cs455/scaling/client/ClientMessageSender.class
	@rm  -rf ./cs455/scaling/client/Client.class
	
compile:
	@echo -e '[INFO] Compiling Sources'
	@javac -d . ./src/cs455/scaling/task/TaskType.java
	@javac -d . ./src/cs455/scaling/task/Task.java
	@javac -d . ./src/cs455/scaling/taskQueue/TaskQueueManager.java
	@javac -d . ./src/cs455/scaling/task/VoidTask.java
	@javac -d . ./src/cs455/scaling/task/WriteTask.java
	@javac -d . ./src/cs455/scaling/task/ReadAndCalculateHash.java
	@javac -d . ./src/cs455/scaling/threadpool/WorkerThread.java
	@javac -d . ./src/cs455/scaling/threadpool/ThreadPoolManager.java
	@javac -d . ./src/cs455/scaling/TaskOrchestrator/TaskDispatcherThread.java
	@javac -d . ./src/cs455/scaling/utils/ValidateCommandLine.java
	@javac -d . ./src/cs455/scaling/server/ConnectionListenerThread.java
	@javac -d . ./src/cs455/scaling/server/Server.java
	@javac -d . ./src/cs455/scaling/client/ClientMessageSender.java
	@javac -d . ./src/cs455/scaling/client/Client.java
	@echo -e '[INFO] Done Compiling Sources'

