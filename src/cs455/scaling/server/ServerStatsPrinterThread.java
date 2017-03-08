package cs455.scaling.server;

import cs455.scaling.task.MessageTracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerStatsPrinterThread implements Runnable {
    private final MessageTracker messageTracker;
    private final ConnectionTracker connectionTracker;

    ServerStatsPrinterThread(final MessageTracker messageTracker, final ConnectionTracker connectionTracker) {
        this.messageTracker = messageTracker;
        this.connectionTracker = connectionTracker;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (true) {
            final long endTime = System.currentTimeMillis();
            if((endTime - startTime) > 5000) {  //Print the stats approximately every 5 seconds on the server.
                printStats();
                startTime = endTime;
            }
        }
    }

    private void printStats() {
        //[timestamp] Current Server Throughput: x messages/s, Active Client Connections: y
        final DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
        final Date date = new Date();
        final float ratePerSecond = messageTracker.getNumMessagesProcessedAndClearCounter()/5;  //calculate the rate per second, ball-par estimate.
        System.out.println("[" + dateFormat.format(date) + "] "
                    + "Current Server Throughput: "+ ratePerSecond+" messages/s, Active Client Connections: " + connectionTracker.getNumConnections());
        System.out.flush();
    }
}
