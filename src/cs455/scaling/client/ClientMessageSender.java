package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientMessageSender implements Runnable {

    private final int messageRate;
    private final SocketChannel client ;
    final String messages = "This is a initial message from Client";

    ClientMessageSender(final int messageRate, final SocketChannel client) {
        this.messageRate = messageRate;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {  //TODO :: Generate random 8KB bytes and send rate
            try {
                Thread.sleep(1000/messageRate);
            } catch (InterruptedException iE) {
                System.out.println("Error : Caught InterruptedException while Thread.sleep");
            }
            ByteBuffer bufferToSend = ByteBuffer.wrap(messages.getBytes());
            try {
                this.client.write(bufferToSend);
            } catch (IOException iOe) {
                System.out.println("Error : IO Exception while sending data to server");
            }
        }
    }
}
