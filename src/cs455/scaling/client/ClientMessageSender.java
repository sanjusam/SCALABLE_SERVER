package cs455.scaling.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.SocketAddress;
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
//    final String messages = "This is a initial message from Client";
    private int cntr = 0;
    final HashHolder hashOfSendData = new HashHolder();

    ClientMessageSender(final int messageRate, final SocketChannel client) {
        this.messageRate = messageRate;
        this.client = client;
    }

    @Override
    public void run() {
        int numMessagesSend = 0;
        int numMessagesReceived = 0;
        long startTime ;
        long endTime;

        startTime = System.currentTimeMillis();
        endTime = 0;
        while (true) {
            final byte[] payloadToSend = generateRandomByteArray();
//            final byte[] payloadToSend =  messages.getBytes();
            final String hashAtClient = SHA1FromBytes(payloadToSend);
            hashOfSendData.addToLinkList(hashAtClient);
            final ByteBuffer bufferToSend = ByteBuffer.wrap(payloadToSend);
            try {
                this.client.write(bufferToSend);
                bufferToSend.clear();
                ++ numMessagesSend;
            } catch (IOException iOe) {
                System.out.println("Error : IO Exception while sending data to server - Exiting");
                System.exit(-1);
            }
            endTime = System.currentTimeMillis();
            if((endTime - startTime) > 10000) {
                //Print Stats
                //[timestamp] Total Sent Count: x, Total Received Count: y
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                System.out.println("[" + dateFormat.format(date) + "]"
                        + " Total Sent Count: " +numMessagesSend
                        + " , Total Received Count: " +numMessagesReceived);
                startTime = endTime;
                numMessagesReceived = 0;
                numMessagesSend = 0;
            }
            try {
                Thread.sleep(1000/messageRate);
            } catch (InterruptedException iE) {
                System.out.println("Info : Caught InterruptedException while Thread.sleep");
            }
            final String hashReceived = readMessageFromServer(client);
            if(hashReceived != null) {
                ++numMessagesReceived;
                final boolean removed = hashOfSendData.checkAndRemovedHash(hashReceived);
                if(!removed) {
                    System.out.println("Warn : Hash from the server does match");
                }
                //TODO :: Remove??
                if(!hashAtClient.equals(hashReceived)) {
                    System.out.println("Hash Matches");
                } else {
                    System.out.println("Warn : Hash from the server does match");
                    System.exit(-1);  //TODO :: Remove, dont want the thread to exit if the has does not match
                }
            }
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
        final ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
        int numRead = -1;
        try {
            numRead = channel.read(byteBuffer);
        } catch (final IOException ioe) {
            System.out.println("Error : IO Exception while reading from server - Exiting");
            System.exit(-1);
        } catch (NegativeArraySizeException nASE) {
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
