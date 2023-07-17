package nom.aob.sbbench.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.xcontent.ToXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ElasticSearchRHLCService {
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchRHLCService.class);

    private RestHighLevelClient client;

    @Autowired
    public ElasticSearchRHLCService(final RestHighLevelClient client){
        this.client = client;
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
            return str;
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
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


}
