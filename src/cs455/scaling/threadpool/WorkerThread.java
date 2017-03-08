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
                myTask.perform();// This is the  where the worker thread actually does the work.  If not a void task, it has something to do.  Either READ-HASH or WRITE.
                myTask = voidTask;  // As soon as the work is completed, the worker, itself declares as its free.
            } catch (IOException iOe) {
                System.out.println("Warn : IO Exception Caught");
            } catch (NoSuchAlgorithmException nSAe) {
                System.out.println("Warn : NoSuchAlgorithmException Exception Caught");
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
