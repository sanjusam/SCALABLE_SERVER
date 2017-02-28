package cs455.scaling.server;

import cs455.scaling.task.ReadAndCalculateHash;
import cs455.scaling.task.Task;
import cs455.scaling.task.WriteTask;
import cs455.scaling.taskQueue.TaskQueueManager;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ConnectionListenerThread implements Runnable {

    private Selector selector;
    private TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();
    ConnectionListenerThread(final Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.selector.select();
            } catch (IOException iOe) {
                System.out.println("ERROR : IO Exception thrown while selector.select");
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
                } else if (key.isReadable()) {
                    this.read(key);
                } else if (key.isWritable()) {
                    this.write(key, new byte[1]); //TODO:: REMOVE??
                }
            }
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

        try {
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            final Socket socket = channel.socket();
            SocketAddress remoteAddress = socket.getRemoteSocketAddress();
            System.out.println("DEBUG : Connected to " + remoteAddress);
            channel.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException iOe) {
            System.out.println("WARN : IO Exception while accept");
        }
    }

    private void read(SelectionKey key) {
        final Task readAndCalculateHashTask = new ReadAndCalculateHash(key);
        System.out.println("DEBUG : Something to read - Adding a task");
        taskQueueManager.addTask(readAndCalculateHashTask);
    }

    private void write(SelectionKey key, byte[] dataToWrite) {
        final Task writeTask = new WriteTask(key, dataToWrite);
        System.out.println("DEBUG : Something to write - Adding a task");
        taskQueueManager.addTask(writeTask);
    }
}
