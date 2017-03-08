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
    private final Queue<Task> taskQueue = new LinkedList<>();

    public synchronized void addTask(final Task task) {
        taskQueue.add(task);
//        System.out.println("SANJU DEBUG : Added task - count is , " + taskQueue.size() + "  " + task.getTaskType());
    }

    public synchronized Task getTask() {
        if(!taskQueue.isEmpty()) {
            final int newSize = taskQueue.size() -1;
//            System.out.println("SANJU DEBUG : Removed task - count is , " + newSize );
            return taskQueue.remove();
        }
        return null;
    }
}
