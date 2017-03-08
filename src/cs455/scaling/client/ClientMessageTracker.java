package cs455.scaling.client;

public class ClientMessageTracker {
    private static ClientMessageTracker INSTANCE = new ClientMessageTracker();

    public static synchronized ClientMessageTracker getInstance(){
        return INSTANCE;
    }

    private int numMessagesSend = 0;
    private int numMessgesReceived = 0;
    private final Object LOCK_SEND = new Object();
    private final Object LOCK_REC = new Object();

    void incrementSendMessage() {
        synchronized (LOCK_SEND) {
            ++ numMessagesSend;
        }
    }

    void incrementReceivedMessage() {
        synchronized (LOCK_REC) {
            ++ numMessgesReceived;
        }
    }

    int getNumSendMessage() {
        synchronized (LOCK_SEND) {
            final int temp = numMessagesSend;
            numMessagesSend = 0;
            return temp;
        }
    }

    int getNumMessgesReceived() {
        synchronized (LOCK_REC) {
            final int temp = numMessgesReceived;
            numMessgesReceived = 0;
            return temp;
        }
    }
}
