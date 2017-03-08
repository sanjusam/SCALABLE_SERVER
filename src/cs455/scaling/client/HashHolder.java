package cs455.scaling.client;

import java.util.LinkedList;
import java.util.List;

/*Package Private access*/
class HashHolder {
    private final List<String> listOfGeneratedHash = new LinkedList<>();

    void addToLinkList(final String hashGenerated) {  // As soon as a has is generated at the client, its kept here.
        listOfGeneratedHash.add(hashGenerated);
    }

    boolean checkAndRemovedHash(final String hasReceived) {  // the has recievied from the server, is checked, removed and the status,  is returned to the caller.
        if(listOfGeneratedHash.contains(hasReceived)) {
            return listOfGeneratedHash.remove(hasReceived);
        }
        return false;
    }
}
