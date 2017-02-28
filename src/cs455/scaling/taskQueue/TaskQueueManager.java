package cs455.scaling.taskQueue;

import cs455.scaling.task.Task;
import java.util.ArrayDeque;
import java.util.Deque;

public class TaskQueueManager {
    private static TaskQueueManager INSTANCE = new TaskQueueManager();
    public static synchronized TaskQueueManager getInstance(){
        return INSTANCE;
    }

    private TaskQueueManager() {

    }
    private Deque<Task> taskQueue = new ArrayDeque<>();

    public synchronized void addTask(final Task task) {
        taskQueue.add(task);
    }

    public synchronized Task getTask() {
        if(!taskQueue.isEmpty()) {
            return taskQueue.getFirst();
        }
        return null;
    }
}
