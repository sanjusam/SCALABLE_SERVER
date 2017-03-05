package cs455.scaling.utils;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostNameUtils {
    public static String getHostFqdn() throws UnknownHostException {
        final InetAddress iAddress = InetAddress.getLocalHost();
        return iAddress.getCanonicalHostName();
    }

    public static String getIPAdress() throws UnknownHostException {
        return InetAddress.getLocalHost().toString();
    }

}
