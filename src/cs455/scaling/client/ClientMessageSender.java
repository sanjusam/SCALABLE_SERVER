package cs455.scaling.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ClientMessageSender implements Runnable {

    private final int messageRate;
    private final SocketChannel client ;
//    final String messages = "This is a initial message from Client";
    int cntr = 0;

    ClientMessageSender(final int messageRate, final SocketChannel client) {
        this.messageRate = messageRate;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            final byte[] payloadToSend = generateRandomByteArray();
//            final byte[] payloadToSend =  messages.getBytes();
            final String hashAtClient = SHA1FromBytes(payloadToSend);
            final ByteBuffer bufferToSend = ByteBuffer.wrap(payloadToSend);
            try {
                System.out.println("DEBUG : Sending data to server : " +  ++cntr);
                this.client.write(bufferToSend);
                bufferToSend.clear();
            } catch (IOException iOe) {
                System.out.println("Error : IO Exception while sending data to server");
            }
            try {
                Thread.sleep(1000/messageRate);
            } catch (InterruptedException iE) {
                System.out.println("Error : Caught InterruptedException while Thread.sleep");
            }
            final String hashReceived = readMessageFromServer(client);

            if(hashAtClient.equals(hashReceived)) {
                System.out.println("Hash Matches");
            } else {
                System.out.println("Hash DOES NOT Match");
                System.exit(-1);
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
            System.out.println("IO Exception while reading ");
        } catch (NegativeArraySizeException nASE) {
            System.out.println("INFO : Server Stopped Sending messages");
            System.exit(-1);
        }

        byte[] dataRead = new byte[numRead];
        System.arraycopy(byteBuffer.array(), 0, dataRead, 0, numRead);
        return new String(dataRead);
    }
}
