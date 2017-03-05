package cs455.scaling.TaskOrchestrator;

import cs455.scaling.task.Task;
import cs455.scaling.taskQueue.TaskQueueManager;
import cs455.scaling.threadpool.ThreadPoolManager;
import cs455.scaling.threadpool.WorkerThread;

public class TaskDispatcherThread implements  Runnable {
    private final TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();
    private final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();

    @Override
    public void run() {
        while (true) {
            final Task taskToDispatch = taskQueueManager.getTask();
            if (taskToDispatch != null) {
                while (true) {
                    final WorkerThread availableWorkerThread = threadPoolManager.getAvailableThread();
                    if (availableWorkerThread != null) {
                        availableWorkerThread.setTask(taskToDispatch); //Got a thread, dispatch the job.
                        break;
                    }
                }
            }
        }
    }
}
