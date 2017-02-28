package cs455.scaling.TaskOrchestrator;

import cs455.scaling.task.Task;
import java.util.ArrayDeque;
import java.util.Deque;

public class TaskQueueManager {
    private Deque<Task> taskQueue = new ArrayDeque<>();

    public synchronized void addToQueue(final Task task) {
        taskQueue.add(task);
    }

    public synchronized Task getFromQueue() {
        return taskQueue.getFirst();
    }
}
