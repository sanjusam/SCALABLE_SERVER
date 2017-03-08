package cs455.scaling.server;

import cs455.scaling.TaskOrchestrator.TaskDispatcherThread;
import cs455.scaling.task.MessageTracker;
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

    final ConnectionTracker clientConnectionTracker = ConnectionTracker.getInstance();
    final MessageTracker messageTracker = MessageTracker.getInstance();


    public static void main (final String[] args) throws IOException {

        validateCommandLine(args);
        initThreadPools();
        final Server server = new Server();
        server.startServer();  //Start the Server.
    }

    private void startServer() throws IOException {
        final String HOST_NAME = HostNameUtils.getHostFqdn();
        this.selector = Selector.open();
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        final InetSocketAddress listenAddr = new InetSocketAddress(HOST_NAME, PORT_NUMBER);
        serverChannel.socket().bind(listenAddr);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        startTaskProcessors();  //Starts all the Worker Thread.
        acceptConnections();
        startStatsPrinter();
        System.out.println("Info : Server started on " + HOST_NAME + ":" + PORT_NUMBER
                            + "\nCtrl-C to stop.");
    }

    private void startStatsPrinter() { //The Task printer thread, responsible fot printing stats.
        final ServerStatsPrinterThread statsPrinter = new ServerStatsPrinterThread(messageTracker, clientConnectionTracker);
        final Thread statusPrinterThread = new Thread(statsPrinter);
        statusPrinterThread.setName("Server Status Printer Thread");
        statusPrinterThread.start();
    }

    private void startTaskProcessors() {  //The worker manager thread
        final TaskDispatcherThread taskDispatcherThread = new TaskDispatcherThread();
        final Thread taskProcessorThread = new Thread(taskDispatcherThread);
        taskProcessorThread.setName("Task Dispatcher Thread");
        taskProcessorThread.start();
    }
    private void acceptConnections() throws IOException {  //the main thread, which accepts connections from the clients
        final ConnectionListenerThread connectionListenerThread = new ConnectionListenerThread(selector, clientConnectionTracker, messageTracker);
        final Thread connectionListener = new Thread(connectionListenerThread);
        connectionListener.setName("Connection Listener Thread");
        connectionListener.start();
    }

    private static void initThreadPools() {  //All the worker threads are started,  with the given size.
        final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        threadPoolManager.setMaxThreadPoolSize(THREAD_POOL_SIZE);
        threadPoolManager.startThreads();
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

}
