package nom.aob.sbbench.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nom.aob.sbbench.model.SimpleResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.xcontent.ToXContent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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

    // ##     FOR TEST PURPOSES ##
    public static String toJsonString(Object object) throws JsonProcessingException {
        if (object.getClass().equals(FieldSortBuilder.class)) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = new HashMap<>();
            FieldSortBuilder fieldSortBuilder = (FieldSortBuilder) object;
            map.put("fieldName", fieldSortBuilder.getFieldName());
            map.put("order", fieldSortBuilder.order().toString());
            object = map;
        } else if (object instanceof ToXContent) {
            String str = Strings.toString((ToXContent)object );
            return unescapeJsonString(str);
        }

        ObjectMapper mapper = new ObjectMapper();
        return unescapeJsonString( mapper.writeValueAsString(object) );
    }

    public static void writeStringToFile(String fileName, String str) throws IOException {
        Path path = Paths.get(fileName);
        byte[] strToBytes = str.getBytes();
        Files.write(path, strToBytes);
    }

    public static void writeJsonToFile(String fileName, Object object) throws IOException {
        String str = toJsonString(object);
        writeStringToFile(fileName, str);
    }

    private static AtomicInteger sequencer = new AtomicInteger(1);

    // ### EOF FOR TEST PURPOSES ###

    public static String unescapeJsonString(String inp) {
        return inp
                .replace(":\"{", ":{")
                .replace("}\",", "},")
                .replace("\\\"", "\"")
                ;
    }
}
