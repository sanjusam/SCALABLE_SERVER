package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ClientMainThread implements Runnable {

    private final int messageRate;
    private final SocketChannel client ;
    private final HashHolder hashOfSendData = HashHolder.getInstance();
    private final ClientMessageTracker messageTracker;

    ClientMainThread(final int messageRate, final SocketChannel client, final ClientMessageTracker messageTracker) {
        this.messageRate = messageRate;
        this.client = client;
        this.messageTracker = messageTracker;
    }

    @Override
    public void run() {
        while (true) {
            final byte[] payloadToSend = generateRandomByteArray();  //Generated the random array of bytes.
            final String hashAtClient = SHA1FromBytes(payloadToSend);
            hashOfSendData.addToLinkList(hashAtClient);  //add locally generated has to the link list.
            final ByteBuffer bufferToSend = ByteBuffer.wrap(payloadToSend);
            try {
                this.client.write(bufferToSend);  //Send the data to the server
                bufferToSend.clear();
                messageTracker.incrementSendMessage();
            } catch (final IOException iOe) {
                System.out.println("Info : Server closed connection - Exiting");
                System.exit(-1);
            }
            try {
                Thread.sleep(1000/messageRate);
            } catch (final InterruptedException iE) {
                System.out.println("Info : Caught InterruptedException while Thread.sleep");
            }
            /*final String hashReceived = readMessageFromServer(client);
            if(hashReceived != null) {
                final boolean removed = hashOfSendData.checkAndRemovedHash(hashReceived);
                if(!removed) {
                    System.out.println("Warn : Hash from the server does not match");
                }
            } else {
                System.out.println("Warn : Hash from the server is null  - Server Exited");
            } */
        }
    }

    private byte[] generateRandomByteArray() {
        final byte[] payLoadToSend = new byte[8192];
        new Random().nextBytes(payLoadToSend);
        return payLoadToSend;
    }

    private String SHA1FromBytes(final byte[] data)  {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (final NoSuchAlgorithmException nSAe) {
            nSAe.printStackTrace();
        }

        final byte[] hash = digest.digest(data);
        final BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }

    /*
    private String readMessageFromServer(final SocketChannel channel) {
        final int expectedSize = 40;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(expectedSize);
        int numRead = -1;
        try {
            numRead = channel.read(byteBuffer);
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
    } */
}
