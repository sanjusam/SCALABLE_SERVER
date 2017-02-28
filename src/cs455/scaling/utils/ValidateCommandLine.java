package cs455.scaling.utils;

public class ValidateCommandLine {

    public static int INVAVLID_NUMBER = -999999999; //Random bad number

    public static boolean validateArgumentCount(final int neededArguments, final String[] args) {
        if(args.length <= neededArguments) {
            return false;
        }
        return true;
    }

    public static boolean isValidNumber(final String stringToCheckIfNumber) {
        try {
            Integer.parseInt(stringToCheckIfNumber);
         } catch (NumberFormatException nFe) {
            return false;
        }
        return true;
    }

    public static int getNumber(final String stringToCovertAsNumber) {
        try {
            return Integer.parseInt(stringToCovertAsNumber);
        } catch (NumberFormatException nFe) {
            return INVAVLID_NUMBER;
        }
    }
}
