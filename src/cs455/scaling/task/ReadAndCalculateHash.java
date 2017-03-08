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
    private static final int INCOMING_MESSAGE_SIZE = 8192;
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
       final ByteBuffer buffer = ByteBuffer.allocate(INCOMING_MESSAGE_SIZE);
        int allBytes = 0;
        buffer.clear();
        try {
            while(buffer.hasRemaining() && !(allBytes >= INCOMING_MESSAGE_SIZE)) {  //Read until all the date is read.
                int readBytes = channel.read(buffer);
                allBytes += readBytes;
                if(readBytes == -1 ) {
                    System.out.println("Error : Failure in reading, client closed.");
                    connectionTracker.decrementConnectionCount();  //This is a potential case of client disconnect, close the connection
                    channel.close();
                    return;
                }
            }
            key.attach(null);
            if(allBytes < INCOMING_MESSAGE_SIZE) {
                return; //Partially read data
            }
            readAndProcessData(buffer);
        } catch (final IOException iOe) {
            System.out.println(Thread.currentThread().getName() + " Error : IO Exception thrown while reading byte buffer, client closed");
            connectionTracker.decrementConnectionCount();
            channel.close();
        }
    }

    private void readAndProcessData(final ByteBuffer buffer) throws NoSuchAlgorithmException {
        final byte[] data = new byte[INCOMING_MESSAGE_SIZE];
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
