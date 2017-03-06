package cs455.scaling.task;

import cs455.scaling.server.ConnectionTracker;
import cs455.scaling.taskQueue.TaskQueueManager;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ReadAndCalculateHash implements Task {
    private final TaskType taskType = TaskType.READ_COMPUTE;
    private SelectionKey key;
    private String dataToSendBack;
    private SocketChannel channel;
    private MessageTracker messageTracker;
    private ConnectionTracker connectionTracker;

//    public ReadAndCalculateHash(final SelectionKey key) {
//        this.key = key;
//    }

    public ReadAndCalculateHash(final SelectionKey key, final SocketChannel channel, final MessageTracker messageTracker,
                                final ConnectionTracker connectionTracker) {
        this.key = key;
        this.channel = channel;
        this.messageTracker = messageTracker;
        this.connectionTracker = connectionTracker;
    }

//    public ReadAndCalculateHash(final SelectionKey key, final ByteBuffer buffer) {
//        this.key = key;
//        this.buffer = buffer;
//    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

//
//    @Override
//    public void perform() throws IOException, NoSuchAlgorithmException {
//        byte[] data = new byte[8192];
//        buffer.flip();
//        buffer.get(data);
//
//        key.interestOps(SelectionKey.OP_WRITE);
//        dataToSendBack = SHA1FromBytes(data);
//        //TODO :: Should I add a new task to write to the same key??
//        final Task writeTask = new WriteTask(key, dataToSendBack.getBytes());
//        TaskQueueManager.getInstance().addTask(writeTask);
//    }

    @Override
    public void perform() throws IOException, NoSuchAlgorithmException {
        final SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int readBytes = 1;
        buffer.clear();
        try {
            while(buffer.hasRemaining() && readBytes > 0) {
                readBytes = channel.read(buffer);
                if(readBytes == -1 ) {
                    System.out.println("Error : Failure in reading, client closed.");
                    connectionTracker.decrementConnectionCount();
                    channel.close();
                    return;
                } else if(readBytes == 8192) {
                    readAndProcessData(buffer);
                } else {
                    return; //Partially ready data??
                }
            }
        } catch (final IOException iOe) {
            System.out.println("Error : IO Exception thrown while reading byte buffer, client closed");
            connectionTracker.decrementConnectionCount();
            channel.close();
            return;
        }
    }

    private void readAndProcessData(final ByteBuffer buffer) throws NoSuchAlgorithmException {
        messageTracker.incrementMessageReceived();
        byte[] data = new byte[8192];
        buffer.flip();
        buffer.get(data);

//        key.interestOps(SelectionKey.OP_WRITE);
        dataToSendBack = SHA1FromBytes(data);
        //TODO :: Should I add a new task to write to the same key??
        final Task writeTask = new WriteTask(key, dataToSendBack.getBytes());
        TaskQueueManager.getInstance().addTask(writeTask);

    }


    @Override
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }

    private String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }
}
