package cs455.scaling.client;

import cs455.scaling.utils.HostNameUtils;
import cs455.scaling.utils.ValidateCommandLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

public class Client {
    private static int MESSAGE_RATE = 0;
    private static int SERVER_PORT = 0;
    private static String SERVER_NAME ;

    private final ClientMessageTracker clientMessageTracker = ClientMessageTracker.getInstance();

    public static void main(final String[] args ) {

        validateInputArguments(args);

        final Client client = new Client();
        final SocketChannel clientChannel = client.startClient();
        if(clientChannel == null) {
            System.out.println("Error : Unable to establish connection to server " + SERVER_NAME + " " + SERVER_PORT);
            System.exit(-1);
        }
        client.startMessageSender(clientChannel);
        client.startStatusPrinterThread();
        client.startDataReceiver(clientChannel);

    }

    private SocketChannel startClient() {
        try {
            final InetSocketAddress hostAddress = new InetSocketAddress(SERVER_NAME, SERVER_PORT);
            return SocketChannel.open(hostAddress);
        } catch (final IOException iOe) {
            System.out.println("Error : IO Exception caught while starting client - Exiting");
            System.exit(-1);
        }
        return null;
    }

    private void startStatusPrinterThread() {
        final ClientStatsPrinterThread clientStatsPrinter = new ClientStatsPrinterThread(clientMessageTracker);
        final Thread clientStatusPrinterThread = new Thread(clientStatsPrinter);
        clientStatusPrinterThread.setName("Client Status Printer");
        clientStatusPrinterThread.start();
    }

    private void startMessageSender(final SocketChannel client) {
        final ClientMainThread clientMainThread = new ClientMainThread(MESSAGE_RATE, client, clientMessageTracker);
        final Thread clientMessageSenderThread = new Thread(clientMainThread);
        clientMessageSenderThread.setName("Client Node");
        clientMessageSenderThread.start();
    }

    private void startDataReceiver(final SocketChannel clientSocketChannel) {
        final ClientDataReceiverThread clientDataReceiver = new ClientDataReceiverThread(clientSocketChannel, clientMessageTracker);
        final Thread clientDataReceiverThread = new Thread(clientDataReceiver);
        clientDataReceiverThread.setName("Client Data Receiver");
        clientDataReceiverThread.start();
    }

    /*Input validation for all the inputs passed  - If not valid input just exit*/

    private static void validateInputArguments(final String[] args) {
        final boolean validArgs = ValidateCommandLine.validateArgumentCount(3, args); // Three arguments needed
        if(!validArgs) {
            System.out.println("Error : Invalid Arguments, Valid arguments are server-host, server-port and messaging rate");
            System.out.flush();
            System.exit(-1);
        }

        if(ValidateCommandLine.isValidNumber(args[1])) {
            SERVER_PORT = ValidateCommandLine.getNumber(args[1]);
        }
        if(SERVER_PORT <= 0) {
            System.out.println("Error : Cannot Start with the given server port: " + args[1]);
            System.exit(-1);
        }

        if(ValidateCommandLine.isValidNumber(args[2])) {
            MESSAGE_RATE = ValidateCommandLine.getNumber(args[2]);
        }
        if(MESSAGE_RATE <= 0) {
                System.out.println("Error : Cannot Start with the given Message rate : " + args[2]);
                System.exit(-1);
        }

        if(args[0].equals("localhost")) { // get the hostname if localhost is passed.
            try {
                SERVER_NAME = HostNameUtils.getHostFqdn();
            } catch (final UnknownHostException e) {
                System.out.println("Error : Server name passed as localhost, pass the server's name");
                System.exit(-1);
            }
        } else {
            SERVER_NAME = args[0];
        }

    }
}
