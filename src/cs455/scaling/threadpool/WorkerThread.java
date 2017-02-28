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
                System.out.println("DEBUG : Performing the task " + myTask.getTaskType().toString() );
                myTask.perform();
                myTask = voidTask;
            } catch (IOException iOe) {
                System.out.println("IO Exception Caught");
                continue;
            } catch (NoSuchAlgorithmException nSAe) {
                System.out.println("NoSuchAlgorithmException Exception Caught");
                continue;
            }
        }
    }

    public void setTask(final Task myTask) {
        System.out.println("DEBUG : Setting Task to " + myTask.getTaskType().toString());
        this.myTask = myTask;
    }

    public Task getTask() {
        return myTask;
    }

}
