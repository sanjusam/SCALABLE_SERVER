package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ClientMessageSender implements Runnable {

    private final int messageRate;
    private final SocketChannel client ;
    private final HashHolder hashOfSendData = new HashHolder();
    private long startTime ;
    private int numMessagesSend ;
    private int numMessagesReceived;

    ClientMessageSender(final int messageRate, final SocketChannel client) {
        this.messageRate = messageRate;
        this.client = client;
        numMessagesSend = 0;
        numMessagesReceived = 0;
        startTime =0;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        while (true) {
            final byte[] payloadToSend = generateRandomByteArray();
            final String hashAtClient = SHA1FromBytes(payloadToSend);
            hashOfSendData.addToLinkList(hashAtClient);
            final ByteBuffer bufferToSend = ByteBuffer.wrap(payloadToSend);
            try {
                this.client.write(bufferToSend);
                bufferToSend.clear();
                ++ numMessagesSend;
            } catch (IOException iOe) {
                System.out.println("Info : Server closed connection - Exiting");
                System.exit(-1);
            }
            printStats();
            try {
                Thread.sleep(1000/messageRate);
            } catch (InterruptedException iE) {
                System.out.println("Info : Caught InterruptedException while Thread.sleep");
            }
            final String hashReceived = readMessageFromServer(client);
            if(hashReceived != null) {
                final boolean removed = hashOfSendData.checkAndRemovedHash(hashReceived);
                if(!removed) {
                    System.out.println("Warn : Hash from the server does not match");
                }
            }
        }
    }

    private void printStats() {
        final long endTime = System.currentTimeMillis();
        if((endTime - startTime) > 10000) {
            //[timestamp] Total Sent Count: x, Total Received Count: y
            final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.println("[" + dateFormat.format(new Date() ) + "]"
                    + " Total Sent Count: " +numMessagesSend
                    + ", Total Received Count: " +numMessagesReceived);
            startTime = endTime;
            numMessagesReceived = 0;
            numMessagesSend = 0;
            System.out.flush();
        }
    }

    private byte[] generateRandomByteArray() {
        final byte[] payLoadToSend = new byte[8192];
        new Random().nextBytes(payLoadToSend);
        return payLoadToSend;
    }

    private String SHA1FromBytes(byte[] data)  {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException nSAe) {
            nSAe.printStackTrace();
        }

        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }

    private String readMessageFromServer(final SocketChannel channel) {
        final int expectedSize = 40;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(expectedSize);
        int numRead = -1;
        try {
            numRead = channel.read(byteBuffer);
            if(numRead >= expectedSize-1) {
                ++numMessagesReceived;
            }
        } catch (final IOException ioe) {
            System.out.println("Error : IO Exception while reading from server - Exiting");
            System.exit(-1);
        } catch (final NegativeArraySizeException nASE) {
            System.out.println("Info : Server Stopped Sending messages - Exiting");
            System.exit(-1);
        }
        if(numRead > 0) {
            byte[] dataRead = new byte[numRead];
            System.arraycopy(byteBuffer.array(), 0, dataRead, 0, numRead);
            return new String(dataRead);
        } else {
            return null;
        }
    }
}
