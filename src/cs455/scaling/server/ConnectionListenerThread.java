package cs455.scaling.server;

import cs455.scaling.task.MessageTracker;
import cs455.scaling.task.ReadAndCalculateHash;
import cs455.scaling.task.Task;
import cs455.scaling.task.WriteTask;
import cs455.scaling.taskQueue.TaskQueueManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ConnectionListenerThread implements Runnable {

    private Selector selector;
    private final ConnectionTracker clientConnectionTracker ;
    private final MessageTracker messageTracker ;
    private long startTime;
    private TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    ConnectionListenerThread(final Selector selector, final ConnectionTracker clientConnectionTracker, final MessageTracker messageTracker) {
        this.selector = selector;
        this.clientConnectionTracker = clientConnectionTracker;
        this.messageTracker = messageTracker;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        while (true) {
//            printStats();
            try {
                this.selector.select();
            } catch (IOException iOe) {
                System.out.println("Error : IO Exception thrown while selector.select");
                continue;
            }
            Iterator keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                final SelectionKey key = (SelectionKey) keys.next();
                keys.remove();
                if (! key.isValid()) {
                    continue;
                } else if (key.isAcceptable()) {
                    this.accept(key);
                } else if (key.isReadable() && (key.attachment() == null)) {
                    this.read(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

        try {
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
//            channel.register(this.selector, SelectionKey.OP_READ);  TODO:: Temporarly removed and added below
//            selector.wakeup();
            if(channel.finishConnect()) {
                channel.register(this.selector, SelectionKey.OP_READ);
            }
            clientConnectionTracker.incrementConnectionCount();
        } catch (IOException iOe) {
            System.out.println("Warn : IO Exception while accept");
        }
    }

    private void read(final SelectionKey key) {
        final Object attachment = new Object();
        final SocketChannel channel = (SocketChannel) key.channel();
        final Task readAndCalculateHashTask = new ReadAndCalculateHash(key, channel, messageTracker, clientConnectionTracker);
        key.attach(attachment);
        taskQueueManager.addTask(readAndCalculateHashTask);
//        key.interestOps(SelectionKey.OP_WRITE);
//        selector.wakeup();
    }

    /*
    private void read(final SelectionKey key) {
        final SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int readBytes = 1;
        buffer.clear();
        try {
            while(buffer.hasRemaining() && readBytes > 0) {
                readBytes = channel.read(buffer);
                if(readBytes == 8192) {
//                    final Task readAndCalculateHashTask = new ReadAndCalculateHash(key, buffer);
                    final Task readAndCalculateHashTask = new ReadAndCalculateHash(key, channel);
                    taskQueueManager.addTask(readAndCalculateHashTask);
                    key.interestOps(SelectionKey.OP_WRITE);
                    selector.wakeup();
                    ++numMessages;
                    break;
                } else if(readBytes == -1) {
                    //Probably a disconnect from client??
//                    if(numConnections > 0) {
//                        --numConnections;
//                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Info : Unable to read from client, probably a client disconnect?");
            if(numConnections > 0) {
                --numConnections;
            }
            try {
                channel.close();
            } catch (IOException ioE) {
            }
            key.cancel();
        }

//        System.out.println("DEBUG : Something to read - Adding a task");
//        taskQueueManager.addTask(readAndCalculateHashTask);
////        try {
//            readDataTest(key);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }

    */

    private void write(SelectionKey key, byte[] dataToWrite) {
        final Task writeTask = new WriteTask(key, dataToWrite, messageTracker);
        taskQueueManager.addTask(writeTask);
    }

    private void printStats() {  //TODO :: Move to a utility??
        //[timestamp] Current Server Throughput: x messages/s, Active Client Connections: y
        final long endTime = System.currentTimeMillis();
        if((endTime - startTime) > 5000) {
            final DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
            final Date date = new Date();
            final float ratePerSecond = messageTracker.getNumMessagesProcessed()/5;
            System.out.println("[" + dateFormat.format(date) + "] "
                    + "Current Server Throughput: "+ ratePerSecond+" messages/s, Active Client Connections: " + clientConnectionTracker.getNumConnections());
            startTime = endTime;
            messageTracker.clearMessageCounter();
            System.out.flush();
        }
    }


//    public void readDataTest(final SelectionKey key) throws IOException, NoSuchAlgorithmException {
//        SocketChannel channel = (SocketChannel) key.channel();
//        ByteBuffer buffer = ByteBuffer.allocate(8192); //TODO :: Adjust size
//        int numRead = -1;
//        try {
//            numRead = channel.read(buffer);
//        }
//        catch (IOException e) {
//            System.out.println("Exception in reading data, Probably client closed connection ");
//        }
//        if (numRead == -1) {
//            Socket socket = channel.socket();
//            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
//            System.out.println("Connection closed by client: " + remoteAddr);
//            channel.close();  //TODO ??
//            key.cancel(); //TODO ??
//        }
//        byte[] data = new byte[numRead];
//        System.arraycopy(buffer.array(), 0, data, 0, numRead);
//        System.out.println("Got: " + new String(data, "US-ASCII"));
//        key.interestOps(SelectionKey.OP_WRITE);
//    }
}
