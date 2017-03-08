package cs455.scaling.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteTask implements Task {
    private final TaskType taskType = TaskType.WRITE;
    private SelectionKey key;
    private final byte[] dataToWrite;
    private final MessageTracker messageTracker;

    public WriteTask(final SelectionKey key, final byte[] dataToWrite, final MessageTracker messageTracker) {
        this.dataToWrite = dataToWrite;
        this.key = key;
        this.messageTracker = messageTracker;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public void perform() throws IOException {  //Details of the WRITE task.
        final SocketChannel channel = (SocketChannel) key.channel();
        channel.write(ByteBuffer.wrap(dataToWrite));
        messageTracker.incrementMessageProcessed();  //Increment message processed counter for printing stats
        key.interestOps(SelectionKey.OP_READ);
    }

    @Override
    public void setSelectionKey(final SelectionKey key) {
        this.key = key;
    }
}
