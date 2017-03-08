package cs455.scaling.taskQueue;

import cs455.scaling.task.Task;
import java.util.LinkedList;
import java.util.Queue;

public class TaskQueueManager {
    private static TaskQueueManager INSTANCE = new TaskQueueManager();
    public static synchronized TaskQueueManager getInstance(){
        return INSTANCE;
    }

    private TaskQueueManager() {

    }
    private final Queue<Task> taskQueue = new LinkedList<>();  //The place holder for the task queues.  All created task gets added here and  would be popped out for execution.

    public synchronized void addTask(final Task task) {
        taskQueue.add(task);
    }

    public synchronized Task getTask() {
        if(!taskQueue.isEmpty()) {  // Returns the next work in the queue to be assigned to a worker.
            return taskQueue.remove();
        }
        return null;
    }
}
