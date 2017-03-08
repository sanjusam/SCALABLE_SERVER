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
            final Task taskToDispatch = taskQueueManager.getTask();  //Gets the next works
            if (taskToDispatch != null) {
                while (true) {  // Waits until a worker becomes available.
                    final WorkerThread availableWorkerThread = threadPoolManager.getAvailableThread();   //Once worker is available, assigns the work to the worker.
                    if (availableWorkerThread != null) {
                        availableWorkerThread.setTask(taskToDispatch); //Got a thread, dispatch the job.
                        break;  //Breaks for the work-worker assignment loop and again goes back to the main loop to look for the new jobs in the queue and assign it to the next worker.
                    }
                }
            }
        }
    }
}
