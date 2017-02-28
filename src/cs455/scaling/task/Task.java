package cs455.scaling.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

public interface Task {
    TaskType getTaskType();
    void perform() throws IOException, NoSuchAlgorithmException;
    void setSelectionKey(SelectionKey key);
}
