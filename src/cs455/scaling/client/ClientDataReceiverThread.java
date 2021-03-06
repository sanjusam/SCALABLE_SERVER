package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientDataReceiverThread implements Runnable {
    private final SocketChannel clientSocketChannel;
    private final ClientMessageTracker messageTracker;
    private final HashHolder hashOfSendData = HashHolder.getInstance();

    ClientDataReceiverThread(final SocketChannel clientSocketChannel, final ClientMessageTracker messageTracker) {
        this.clientSocketChannel = clientSocketChannel;
        this.messageTracker = messageTracker;
    }

    @Override
    public void run() {
        while (true) {
            if(messageTracker.sendStarted()) {  //Wait until the first message is send from the client.
                break;
            }
        }
        /* Loops to receive data until the server closes the connection.  When the server closes the connection
        * there are chances, that the there is a null/bad data read, and causes the thread to print "Hash from the server is null - Server Exited."*/
        while (true) {
            final String hashReceived = readMessageFromServer();
            if (hashReceived != null) {
                final boolean removed = hashOfSendData.checkAndRemovedHash(hashReceived);
                if (!removed) {
                    System.out.println("Warn : Hash from the server does not match");
                }
            } else {
                System.out.println("Info : Hash from the server is null - Server Exited");
            }
        }
    }


    private String readMessageFromServer() {
        final int expectedSize = 40;  // Length of the hash, coming from the server is mostly 40, and in rare cases its 39.
        final ByteBuffer byteBuffer = ByteBuffer.allocate(expectedSize);
        int numRead = -1;
        try {
            numRead = clientSocketChannel.read(byteBuffer);
            if(numRead >= expectedSize-1) {
                messageTracker.incrementReceivedMessage();
            }
        } catch (final IOException ioe) {
            System.out.println("Error : IO Exception while reading from server - Exiting");
            System.exit(-1);
        } catch (final NegativeArraySizeException nASE) {
            System.out.println("Info : Server Stopped Sending messages - Exiting");
            System.exit(-1);
        }
        if(numRead > 0) {
            final byte[] dataRead = new byte[numRead];
            System.arraycopy(byteBuffer.array(), 0, dataRead, 0, numRead);
            return new String(dataRead);
        } else {
            return null;
        }
    }
}
