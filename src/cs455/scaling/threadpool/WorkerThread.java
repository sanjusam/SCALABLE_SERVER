package cs455.scaling.threadpool;

import cs455.scaling.task.Task;
import cs455.scaling.task.TaskType;
import cs455.scaling.task.VoidTask;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class WorkerThread implements Runnable {

    private volatile Task myTask;
    private final Task voidTask = new VoidTask();
    WorkerThread(final Task myTask) {
        this.myTask = myTask;
    }

    @Override
    public void run() {
        while (true) {
            if(myTask.getTaskType() == TaskType.VOID_TASK) {
                continue;
            }
            try {
                myTask.perform();
                myTask = voidTask;
            } catch (IOException iOe) {
                System.out.println("Warn : IO Exception Caught");
                continue;
            } catch (NoSuchAlgorithmException nSAe) {
                System.out.println("Warn : NoSuchAlgorithmException Exception Caught");
                continue;
            }
        }
    }

    public synchronized void setTask(final Task myTask) {
        this.myTask = myTask;
    }

    public synchronized Task getTask() {
        return myTask;
    }

}
