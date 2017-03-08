package cs455.scaling.server;

import cs455.scaling.task.MessageTracker;
import cs455.scaling.task.ReadAndCalculateHash;
import cs455.scaling.task.Task;
import cs455.scaling.taskQueue.TaskQueueManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ConnectionListenerThread implements Runnable {

    private Selector selector;
    private final ConnectionTracker clientConnectionTracker ;
    private final MessageTracker messageTracker ;
    private TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    ConnectionListenerThread(final Selector selector, final ConnectionTracker clientConnectionTracker, final MessageTracker messageTracker) {
        this.selector = selector;
        this.clientConnectionTracker = clientConnectionTracker;
        this.messageTracker = messageTracker;
    }

    @Override
    public void run() {
        while (true) {  /*DOC : The main loop that takes in incoming connections, connects, and recreate task for  reading data if there is data to be read.*/
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

    private void read(final SelectionKey key) {
        final Object attachment = new Object();
        final SocketChannel channel = (SocketChannel) key.channel();
        final Task readAndCalculateHashTask = new ReadAndCalculateHash(key, channel, messageTracker, clientConnectionTracker);
        key.attach(attachment);
        taskQueueManager.addTask(readAndCalculateHashTask);  // There is something to be read from the channel, create a read task.
    }


    private void accept(SelectionKey key) {  // Accespt the connection.
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

        try {
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            if(channel.finishConnect()) {
                channel.register(this.selector, SelectionKey.OP_READ);
            }
            clientConnectionTracker.incrementConnectionCount();
        } catch (IOException iOe) {
            System.out.println("Warn : IO Exception while accept");
        }
    }


}
