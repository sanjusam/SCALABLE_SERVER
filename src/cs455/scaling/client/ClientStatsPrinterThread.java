package cs455.scaling.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientStatsPrinterThread implements Runnable{

    private final ClientMessageTracker messageTracker ;

    ClientStatsPrinterThread(final ClientMessageTracker messageTracker) {
        this.messageTracker = messageTracker;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while(true) {
            final long endTime = System.currentTimeMillis();
            if((endTime - startTime) > 10000) {
                printStats();
                startTime = endTime;
            }
        }
    }


    private void printStats() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("[" + dateFormat.format(new Date() ) + "]"
            + " Total Sent Count: " + messageTracker.getNumSendMessage()
            + ", Total Received Count: " + messageTracker.getNumMessagesReceived());
            System.out.flush();
    }
}
