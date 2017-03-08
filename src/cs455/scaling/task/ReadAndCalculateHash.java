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
    private final SocketChannel channel;
    private final MessageTracker messageTracker;
    private final ConnectionTracker connectionTracker;

    public ReadAndCalculateHash(final SelectionKey key, final SocketChannel channel, final MessageTracker messageTracker,
                                final ConnectionTracker connectionTracker) {
        this.key = key;
        this.channel = channel;
        this.messageTracker = messageTracker;
        this.connectionTracker = connectionTracker;
    }


    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public void perform() throws IOException, NoSuchAlgorithmException {
       final ByteBuffer buffer = ByteBuffer.allocate(8192);
        int readBytes = 1;
        buffer.clear();
        try {
            while(buffer.hasRemaining() && readBytes > 0) {
                readBytes = channel.read(buffer);
                key.attach(null);
                if(readBytes == -1 ) {
                    System.out.println("Error : Failure in reading, client closed.");
                    connectionTracker.decrementConnectionCount();  //This is a potential case of client disconnect, close the connection
                    channel.close();
                    return;
                } else if(readBytes == 8192) {
                    readAndProcessData(buffer);  //Read the right set of data, proceed to hashing a a new task for writing.
                } else {
                    return; //Partially ready data??
                }
            }
        } catch (final IOException iOe) {
            System.out.println(Thread.currentThread().getName() + " Error : IO Exception thrown while reading byte buffer, client closed");
            connectionTracker.decrementConnectionCount();
            channel.close();
        }
    }

    private void readAndProcessData(final ByteBuffer buffer) throws NoSuchAlgorithmException {
        final byte[] data = new byte[8192];
        buffer.flip();
        buffer.get(data);
        messageTracker.incrementMessageProcessed();  //Increment message processed counter for printing stats
        final String dataToSendBack = SHA1FromBytes(data);
        final Task writeTask = new WriteTask(key, dataToSendBack.getBytes(), messageTracker);
        TaskQueueManager.getInstance().addTask(writeTask);  //Adds a new task for writing the data back to the client
    }


    @Override
    public void setSelectionKey(final SelectionKey key) {
        this.key = key;
    }

    private String SHA1FromBytes(final byte[] data) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA1");
        final byte[] hash = digest.digest(data);
        final BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }
}
