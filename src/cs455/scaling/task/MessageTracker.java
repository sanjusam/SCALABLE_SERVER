package cs455.scaling.task;

public class MessageTracker {

    private int numMessages = 0;
    private static MessageTracker INSTANCE = new MessageTracker();
    public static synchronized MessageTracker getInstance(){
        return INSTANCE;
    }

    public synchronized void incrementMessageProcessed() {  //This is a sum of all read from client and written to client messages.
        ++numMessages;
    }

    public synchronized float getNumMessagesProcessedAndClearCounter() {
        final int tempMsgCount = numMessages;  // Since num messages is all read and writes,  send back the average for through put and clear counters.
        numMessages = 0;
        return tempMsgCount/2;
    }

}
