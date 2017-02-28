package cs455.scaling.threadpool;

import cs455.scaling.task.Task;
import cs455.scaling.task.TaskType;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class WorkerThread implements Runnable {

    private Task myTask;
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
            } catch (IOException iOe) {
                System.out.println("IO Exception Caught");
            } catch (NoSuchAlgorithmException nSAe) {
                System.out.println("NoSuchAlgorithmException Exception Caught");
            }

        }
    }

    public void setTask(final Task myTask) {
        this.myTask = myTask;
    }
}
