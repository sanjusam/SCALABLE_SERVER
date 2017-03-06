package cs455.scaling.task;

public class MessageTracker {

    private int numMessages = 0;
    private static MessageTracker INSTANCE = new MessageTracker();
    public static synchronized MessageTracker getInstance(){
        return INSTANCE;
    }

    public synchronized void incrementMessageReceived() {
        ++numMessages;
    }

    public int getNumMessages() {
        return numMessages;
    }

    public void clearMessageCounter() {
        numMessages = 0;
    }
}
