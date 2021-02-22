package nom.aob.sbbench.controller.rest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nom.aob.sbbench.model.SimpleResponse;
import nom.aob.sbbench.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping(SimpleRestController.PATH)
public class SimpleRestController {

    private final RestTemplate restTemplate;

    public static final String PATH_SIMPLE = "/simple";
    public static final String PATH_PROXY = "/proxy";
    public static final String PATH = "/rest";
    public static final String SCHEME_HTTP = "http://";

    @Value("${nom.aob.sbbench.logstring:#{null}}")
    private String logString;

    @Autowired
    public SimpleRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping(PATH_SIMPLE)
    public @NonNull ResponseEntity<SimpleResponse> simpleResponse(
            @RequestHeader(value = "x-b3-traceid", required = false) String traceId) {

//        log.info("in simpleResponse: " + traceId);
        if (logString != null) {
            log.info("in simpleResponse. logString = {}.",logString);
        }

        SimpleResponse simpleResponse = Utils.newSimpleResponse(PATH_SIMPLE);
//                SimpleResponse.builder()
//                .hostString(Utils.getHostname())
//                .pathString(PATH_SIMPLE)
//                .timeString(Utils.getCurrentTimeString())
//                .randomInteger(Utils.newRandomInt())
//                .threadID(Thread.currentThread().getId())
//                .build();

        return new ResponseEntity<>(simpleResponse, HttpStatus.OK);
    }

    @GetMapping(PATH_PROXY+"/{address}/{port}")
    public @NonNull ResponseEntity<SimpleResponse> simpleProxy(
            @RequestHeader(value = "x-b3-traceid", required = false) String traceId,
            @PathVariable("address") String address,
            @PathVariable("port") Integer port) {

//        log.info("in simpleProxy: " + traceId);
        if (logString != null) {
            log.info("in simpleProxy. logString = {}.", logString);
        }

        StringBuilder sb = new StringBuilder(SCHEME_HTTP);
        sb.append(address).append(":").append(port).append(PATH).append(PATH_SIMPLE);
        String resourceUrl = sb.toString();

        ResponseEntity<SimpleResponse> response
                = restTemplate.getForEntity(resourceUrl, SimpleResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            SimpleResponse simpleResponse = response.getBody();
            simpleResponse.setPathString(PATH_PROXY + "/" + address + "/" + port);
            return new ResponseEntity<>(simpleResponse, response.getStatusCode());
        }

        return response;
    }

}
