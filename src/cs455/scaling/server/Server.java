package cs455.scaling.server;

import cs455.scaling.threadpool.ThreadPoolManager;
import cs455.scaling.utils.ValidateCommandLine;

public class Server {

    public static void main (String args[]) {
        int THREAD_POOL_SIZE = 0;
        if(!ValidateCommandLine.validateArgumentCount(1, args)) { //One argument required
            System.out.println("Thread pool size should be passed as an argument.");
            System.exit(-1);
        }

        if(ValidateCommandLine.isValidNumber(args[0])) {
            THREAD_POOL_SIZE = ValidateCommandLine.getNumber(args[0]);
        }

        if(THREAD_POOL_SIZE <= 0) {
                System.out.println("Cannot Start with the given thread pool : " + args[0]);
                System.exit(-1);
        }


        final ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        threadPoolManager.setMaxThreadPoolSize(THREAD_POOL_SIZE);
        threadPoolManager.startThreads();

        //TODO :: Thread to wait for connections??
    }
}
