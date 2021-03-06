package cs455.scaling.threadpool;

import cs455.scaling.task.TaskType;
import cs455.scaling.task.VoidTask;

import java.util.ArrayList;
import java.util.List;

public class ThreadPoolManager {
    private int maxThreadPoolSize;

    private List<WorkerThread> workerThreadList;

    private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    public static synchronized ThreadPoolManager getInstance(){
        return INSTANCE;
    }

    public synchronized void setMaxThreadPoolSize (final int maxThreadPoolSize) {
        if(INSTANCE.maxThreadPoolSize == 0) {
            INSTANCE.maxThreadPoolSize = maxThreadPoolSize;
            workerThreadList = new ArrayList<>(INSTANCE.maxThreadPoolSize);
        }
    }

    private ThreadPoolManager() {
    }

    public void startThreads() {
        for(int numThreads = 0; numThreads < maxThreadPoolSize; numThreads++ ) {
            final WorkerThread worker = new WorkerThread(new VoidTask());
            workerThreadList.add(worker);
            final Thread workerThread = new Thread(worker);
            workerThread.setName("Worker-" +(numThreads + 1));
            workerThread.start();
        }
    }

    public WorkerThread getAvailableThread() {  //The Thread is defined to be available (back in the pool), if it has a void task.
        for(WorkerThread workerThread : workerThreadList) {  // Walks through the pool and checks if a thread has a VOID task.  If yes, its an available worker - returns the reference
            if(workerThread.getTask().getTaskType() == TaskType.VOID_TASK) {
                return workerThread;
            }
        }
        return null;
    }

}
