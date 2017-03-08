package cs455.scaling.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteTask implements Task {
    private final TaskType taskType = TaskType.WRITE;
    private SelectionKey key;
    private byte[] dataToWrite;
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
    public void perform() throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.write(ByteBuffer.wrap(dataToWrite));
        messageTracker.incrementMessageProcessed();
        key.interestOps(SelectionKey.OP_READ);
    }

    @Override
    public void setSelectionKey(final SelectionKey key) {
        this.key = key;
    }
}
