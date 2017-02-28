package cs455.scaling.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

public class VoidTask implements  Task {

    private final TaskType taskType = TaskType.VOID_TASK;

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
