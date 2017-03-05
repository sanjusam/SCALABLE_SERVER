package cs455.scaling.client;

import java.util.LinkedList;
import java.util.List;

/*Package Private access*/
class HashHolder {
    private final List<String> listOfGeneratedHash = new LinkedList<>();

    void addToLinkList(final String hashGenerated) {
        listOfGeneratedHash.add(hashGenerated);
    }

    boolean checkAndRemovedHash(final String hasReceived) {
        if(listOfGeneratedHash.contains(hasReceived)) {
            return listOfGeneratedHash.remove(hasReceived);
        }
        return false;
    }
}
