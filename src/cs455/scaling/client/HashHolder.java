package cs455.scaling.client;

import java.util.LinkedList;
import java.util.List;

/*Package Private access*/
class HashHolder {
    private final List<String> listOfGeneratedHash = new LinkedList<>();  // Link list to hold the hash locally generated at the clint
    private static HashHolder INSTANCE = new HashHolder();
    public static synchronized HashHolder getInstance(){
        return INSTANCE;
    }

    private HashHolder() {
    }

    synchronized void addToLinkList(final String hashGenerated) {  // As soon as a has is generated at the client, its kept here.
        listOfGeneratedHash.add(hashGenerated);
    }

    synchronized boolean checkAndRemovedHash(final String hasReceived) {  // the hash received from the server, is checked, removed and the status,  is returned to the caller.
        if(listOfGeneratedHash.contains(hasReceived)) {
            return listOfGeneratedHash.remove(hasReceived);  //Returns true if the has is successfully removed.
        }
        return false;
    }
}
