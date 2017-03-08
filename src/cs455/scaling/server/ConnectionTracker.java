package cs455.scaling.server;

public class ConnectionTracker {
    private int numConnections = 0;

    private static ConnectionTracker INSTANCE = new ConnectionTracker();  //The class that keeps track of the connection.
    public static synchronized ConnectionTracker getInstance(){
        return INSTANCE;
    }

    public void incrementConnectionCount() {
        ++ this.numConnections;
    }

    public synchronized void decrementConnectionCount() {
        -- this.numConnections;
    }

    public int getNumConnections() {
        return this.numConnections;
    }
}
