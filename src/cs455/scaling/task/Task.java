package cs455.scaling.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;

public interface Task {  //All task, Void ReadAndHash, Write implements this interface, so its easier for calling the perform() at the interface level
    TaskType getTaskType();
    void perform() throws IOException, NoSuchAlgorithmException;
    void setSelectionKey(SelectionKey key);
}
