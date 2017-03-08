package cs455.scaling.client;

public class ClientMessageTracker {  //Class that track the messages send and received from the server. It has two different lock mechanism to lock while incrementing and getting the count.
    private static ClientMessageTracker INSTANCE = new ClientMessageTracker();

    public static synchronized ClientMessageTracker getInstance(){
        return INSTANCE;
    }

    private int numMessagesSend = 0;
    private int numMessagesReceived = 0;
    private boolean sendStarted = true;
    private final Object LOCK_SEND = new Object();
    private final Object LOCK_REC = new Object();

    boolean sendStarted() {
        return sendStarted;
    }

    void incrementSendMessage() {
        synchronized (LOCK_SEND) {
            ++ numMessagesSend;
        }
        sendStarted = true;
    }

    void incrementReceivedMessage() {
        synchronized (LOCK_REC) {
            ++numMessagesReceived;
        }
    }

    int getNumSendMessage() {  //This does the clear of the counters too.  In this way, I can avoid a separate transaction to lock and increment.
        synchronized (LOCK_SEND) {
            final int temp = numMessagesSend;
            numMessagesSend = 0;
            return temp;
        }
    }

    int getNumMessagesReceived() {  //This does the clear of the counters too.
        synchronized (LOCK_REC) {
            final int temp = numMessagesReceived;
            numMessagesReceived = 0;
            return temp;
        }
    }
}
