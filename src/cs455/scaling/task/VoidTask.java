package cs455.scaling.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

public class VoidTask implements  Task {

    private final TaskType taskType = TaskType.VOID_TASK;  //This is a place holder TASK, to denote that a thread is available.  Any thread, holding with job denotes that its availabe for the next job.

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public void perform() throws IOException, NoSuchAlgorithmException {

    }

    @Override
    public void setSelectionKey(SelectionKey key) {

    }
}
