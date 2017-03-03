package cs455.scaling.task;

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
    private ByteBuffer buffer;

    public ReadAndCalculateHash(final SelectionKey key) {
        this.key = key;
    }

    public ReadAndCalculateHash(final SelectionKey key, final ByteBuffer buffer) {
        this.key = key;
        this.buffer = buffer;

    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }


    @Override
    public void perform() throws IOException, NoSuchAlgorithmException {
        byte[] data = new byte[8192];
        buffer.flip();
        buffer.get(data);

        key.interestOps(SelectionKey.OP_WRITE);
        dataToSendBack = SHA1FromBytes(data);
        //TODO :: Should I add a new task to write to the same key??
        final Task writeTask = new WriteTask(key, dataToSendBack.getBytes());
        TaskQueueManager.getInstance().addTask(writeTask);

    }

//    @Override
//    public void perform() throws IOException, NoSuchAlgorithmException {
//        SocketChannel channel = (SocketChannel) key.channel();
//        ByteBuffer buffer = ByteBuffer.allocate(8192); //TODO :: Adjust size
//        int readBytes = 0;
//        buffer.clear();
//        try {
//            while(buffer.hasRemaining() && readBytes != -1) {
//                readBytes = channel.read(buffer);
//            }
//        } catch (final IOException iOe) {
//            System.out.println("Error : IO Exception thrown while reading byte buffer");
//            return;
//        }
//
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
