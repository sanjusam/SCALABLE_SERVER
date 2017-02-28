package cs455.scaling.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteTask implements Task {
    private final TaskType taskType = TaskType.WRITE;
    private SelectionKey key;
    private byte[] dataToWrite;

    WriteTask(final byte[] dataToWrite, final SelectionKey key) {
        this.dataToWrite = dataToWrite;
        this.key = key;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public byte[] perform() throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.write(ByteBuffer.wrap(dataToWrite));
        key.interestOps(SelectionKey.OP_READ);
        return null;
    }

    @Override
    public void setSelectionKey(final SelectionKey key) {
        this.key = key;
    }
}
