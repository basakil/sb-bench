package nom.aob.sbbench.utils;

import nom.aob.sbbench.model.SimpleResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class Utils {

    private static String HOSTNAME = null;

    private static final Object HOSTNAME_SYNC = new Object();

    private static final Random RANDOM = new Random();

    public static String getHostname() {
        if (HOSTNAME == null) {
            synchronized (HOSTNAME_SYNC) {
                // no need to double check, temporary overwriting will not make any harm..
                HOSTNAME = findHostName();
            }
        }

        return HOSTNAME;
    }

    private static String findHostName() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
//            e.printStackTrace();
            hostname = null;
        }
        return hostname;
    }

    public static String getCurrentTimeString() {
        long millis = System.currentTimeMillis();

        return Long.toString(millis);
    }

    public static int newRandomInt() {
//        return new Random().nextInt(1000000);
        return RANDOM.nextInt();
    }

    public static SimpleResponse newSimpleResponse(String path) {
        return new SimpleResponse(
                getHostname(),
                path,
                getCurrentTimeString(),
                newRandomInt(),
                Thread.currentThread().getId()
        );
    }

}
