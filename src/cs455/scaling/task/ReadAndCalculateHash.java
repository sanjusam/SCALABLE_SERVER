package cs455.scaling.task;

import cs455.scaling.taskQueue.TaskQueueManager;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ReadAndCalculateHash implements Task {
    private final TaskType taskType = TaskType.READ_COMPUTE;
    private SelectionKey key;
    private byte[] dataToSendBack;

    public ReadAndCalculateHash(final SelectionKey key) {
        this.key = key;
    }
    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public void perform() throws IOException, NoSuchAlgorithmException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192); //TODO :: Adjust size
        int numRead = -1;
        try {
            numRead = channel.read(buffer);
        }
        catch (IOException e) {
            System.out.println("Exception in reading data, Probably client closed connection ");
        }
        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            System.out.println("Connection closed by client: " + remoteAddr);
            channel.close();  //TODO ??
            key.cancel(); //TODO ??
        }
        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        System.out.println("Got: " + new String(data, "US-ASCII"));
        key.interestOps(SelectionKey.OP_WRITE);
        dataToSendBack = SHA1FromBytes(data);
        //TODO :: Should I add a new task to write to the same key??
        final Task writeTask = new WriteTask(key, dataToSendBack);
        TaskQueueManager.getInstance().addTask(writeTask);
    }

    @Override
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }

    private byte[] SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        return digest.digest(data);
    }
}
