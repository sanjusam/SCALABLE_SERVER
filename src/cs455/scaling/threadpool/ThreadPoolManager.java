package cs455.scaling.threadpool;

import cs455.scaling.task.TaskType;
import cs455.scaling.task.VoidTask;

import java.util.ArrayList;
import java.util.List;

public class ThreadPoolManager {
    private int maxThreadPoolSize;

    private List<WorkerThread> workerThreadList;

    private static ThreadPoolManager INSTANCE = new ThreadPoolManager();
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
            Thread workerThread = new Thread(worker);
            workerThread.setName("Worker-" +numThreads + 1);
            workerThread.start();
        }
    }

    public WorkerThread getAvailableThread() {
        for(WorkerThread workerThread : workerThreadList) {
            if(workerThread.getTask().getTaskType() == TaskType.VOID_TASK) {
                return workerThread;
            }
        }
        return null;
    }

}
