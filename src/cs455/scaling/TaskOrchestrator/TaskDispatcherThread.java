package cs455.scaling.TaskOrchestrator;

import cs455.scaling.task.Task;
import cs455.scaling.task.TaskType;
import cs455.scaling.taskQueue.TaskQueueManager;
import cs455.scaling.threadpool.ThreadPoolManager;
import cs455.scaling.threadpool.WorkerThread;

import java.util.List;

public class TaskDispatcherThread implements  Runnable {
    final TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();
    final List<WorkerThread> workerThreadList = ThreadPoolManager.getInstance().getThreadList();

    @Override
    public void run() {
        System.out.println("Starting the TaskDispatcherThread");
        while (true) {
            final Task taskToDispatch = taskQueueManager.getTask();
            if (taskToDispatch != null) {
                System.out.println("DEBUG : GOT TASK for despatch...." + taskToDispatch.getTaskType().toString());
                while (true) {
                    final WorkerThread workerThread = getAvailableThread();
                    if (workerThread != null) {
                        System.out.println("DEBUG : Setting task to thread....");
                        workerThread.setTask(taskToDispatch); //Got a thread, dispatch the job.
                        break;
                    }
                }
            }
        }
    }

    private WorkerThread getAvailableThread() {
        for(WorkerThread workerThread : workerThreadList) {
            if(workerThread.getTask().getTaskType() == TaskType.VOID_TASK) {
                System.out.println("DEBUG : Returning an available thread...");
                return workerThread;
            }
        }
        return null;
    }
}
