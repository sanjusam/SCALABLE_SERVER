package cs455.scaling.client;

import cs455.scaling.utils.ValidateCommandLine;

public class Client {

    public static void main(String args []) {
        int MESSAGE_RATE = 0;
        final boolean validArgs = ValidateCommandLine.validateArgumentCount(3, args); // Three arguments needed
        if(!validArgs) {
            System.out.println("Invalid Arguments: Valid arguments are server-host, server-port and messaging rate");
            System.out.flush();
            System.exit(-1);
        }

        if(ValidateCommandLine.isValidNumber(args[2])) {
            MESSAGE_RATE = ValidateCommandLine.getNumber(args[2]);
        }

        if(MESSAGE_RATE <= 0) {
                System.out.println("Cannot Start with the given Message rate : " + args[2]);
                System.exit(-1);
        }

        //TODO : Start messaging
    }
}
