package cs455.scaling.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientStatsPrinterThread implements Runnable {  //This thread would keep running from start of the client until, the program dies. 

    private final ClientMessageTracker messageTracker ;

    ClientStatsPrinterThread(final ClientMessageTracker messageTracker) {
        this.messageTracker = messageTracker;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while(true) {
            final long endTime = System.currentTimeMillis();
            if((endTime - startTime) > 10000) {  //The client is supposed to print the stats once in 10 seconds,   This would give an approximate time for printing
                printStats();
                startTime = endTime;
            }
        }
    }


    private void printStats() {
        final DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
        System.out.println("[" + dateFormat.format(new Date() ) + "]"
            + " Total Sent Count: " + messageTracker.getNumSendMessage()
            + ", Total Received Count: " + messageTracker.getNumMessagesReceived());
            System.out.flush();
    }
}
