package cs455.scaling.client;

import java.util.LinkedList;
import java.util.List;

public class HashHolder {
    private final List<String> listOfGeneratedHash = new LinkedList<>();

    public void addToLinkList(final String hashGenerated) {
        listOfGeneratedHash.add(hashGenerated);
    }

    public boolean checkAndRemovedHash(final String hasReceived) {
        if(listOfGeneratedHash.contains(hasReceived)) {
            return listOfGeneratedHash.remove(hasReceived);
        }
        return false;
    }
}
