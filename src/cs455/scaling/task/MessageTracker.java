package cs455.scaling.task;

public class MessageTracker {

    private int numMessages = 0;
    private static MessageTracker INSTANCE = new MessageTracker();
    public static synchronized MessageTracker getInstance(){
        return INSTANCE;
    }

    public synchronized void incrementMessageProcessed() {
        ++numMessages;
    }

    public int getNumMessagesProcessed() {
        return numMessages;
    }

    public void clearMessageCounter() {
        numMessages = 0;
    }
}
