package cs455.scaling.server;

import cs455.scaling.TaskOrchestrator.TaskDispatcherThread;
import cs455.scaling.threadpool.ThreadPoolManager;
import cs455.scaling.utils.HostNameUtils;
import cs455.scaling.utils.ValidateCommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server {

    private static int THREAD_POOL_SIZE = 0;
    private static int PORT_NUMBER = 0;
    private Selector selector;

    public static void main (String args[]) throws IOException {

        validateCommandLine(args);
        initThreadPools();
        final Server server = new Server();
        server.startServer();
    }

    private void startServer() throws IOException {
        final String HOST_NAME = HostNameUtils.getHostFqdn();
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        InetSocketAddress listenAddr = new InetSocketAddress(HOST_NAME, PORT_NUMBER);
        serverChannel.socket().bind(listenAddr);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        startTaskProcessors();
        acceptConnections();
        System.out.println("Info : Server started on " + HOST_NAME + ":" + PORT_NUMBER  + " Ctrl-C to stop.");
    }

    private void startTaskProcessors() {
        TaskDispatcherThread taskDispatcherThread = new TaskDispatcherThread();
        Thread taskProcessorThread = new Thread(taskDispatcherThread);
        taskProcessorThread.start();
    }
    private void acceptConnections() throws IOException {
        final ConnectionListenerThread connectionListenerThread = new ConnectionListenerThread(selector);
        Thread connectionListener = new Thread(connectionListenerThread);
        connectionListener.start();
    }

    private static void validateCommandLine(final String[] args) {
        if(!ValidateCommandLine.validateArgumentCount(2, args)) { //two argument required
            System.out.println("Error : Invalid Arguments, Valid arguments are port-number and thread-pool-size");
            System.exit(-1);
        }

        if(ValidateCommandLine.isValidNumber(args[0])) {
            PORT_NUMBER = ValidateCommandLine.getNumber(args[0]);
        }
        if(PORT_NUMBER <= 0) {
            System.out.println("Error :Cannot Start with the given port Number : " + args[0]);
            System.exit(-1);
        }

        if(ValidateCommandLine.isValidNumber(args[1])) {
            THREAD_POOL_SIZE = ValidateCommandLine.getNumber(args[1]);
        }
        if(THREAD_POOL_SIZE <= 0) {
            System.out.println("Error : Cannot Start with the given thread pool : " + args[1]);
            System.exit(-1);
        }
    }

    private static void initThreadPools() {
        final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        threadPoolManager.setMaxThreadPoolSize(THREAD_POOL_SIZE);
        threadPoolManager.startThreads();
    }
}
