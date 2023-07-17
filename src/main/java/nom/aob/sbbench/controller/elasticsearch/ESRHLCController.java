package nom.aob.sbbench.controller.elasticsearch;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nom.aob.sbbench.model.SimpleResponse;
import nom.aob.sbbench.utils.Utils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(ESRHLCController.PATH)
public class ESRHLCController {

    public static final String PATH = "/es";
    public static final String PATH_TEST = "/test";
    public static final String SCHEME_HTTP = "http://";

    private static Logger logger = LoggerFactory.getLogger(ESRHLCController.class);

    private RestHighLevelClient rhlc;

    @Autowired
    public ESRHLCController(RestHighLevelClient rhlc) {
        this.rhlc = rhlc;
    }

    @Value("${nom.aob.sbbench.logstring:#{null}}")
    private String logString;

    @GetMapping(PATH_TEST)
    public @NonNull ResponseEntity<ArrayList<String>> simpleResponse(
            @RequestHeader(value = "x-b3-traceid", required = false) String traceId) throws IOException {

//        log.info("in simpleResponse: " + traceId);
        if (logString != null) {
            logger.info("in {}.{}. logString = {}.", PATH, PATH_TEST, logString);
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        SearchSourceBuilder builder = new SearchSourceBuilder()
                .from(0)
                .size(10);
//                .postFilter(query);


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(builder);

/*
    // @TODO: #ERASEME only for test purposes:
    String fileNameSuffix = "" + new Date().getTime()+".json";
    String fileNamePrefix = "testFiles/";
    writeJsonToFile(fileNamePrefix+"searchRequest-"+fileNameSuffix, searchRequest);
    writeJsonToFile(fileNamePrefix+"query-"+fileNameSuffix, query);
    writeJsonToFile(fileNamePrefix+"fieldSortBuilder-"+fileNameSuffix, sortBuilder);
*/
        ArrayList<String> results = new ArrayList<>(50);
        SearchResponse elasticsearchResponse = this.rhlc.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : elasticsearchResponse.getHits()) {
            try {
        /*
        businessLogList.add(

                BusinessLogHelper.toBusinessLog(
                        BusinessLogHelper.toElasticSearchLog(hit.getSourceAsString()
                                .replace(":\"{", ":{")
                                .replace("}\",", "},")
                                .replace("\\\"", "\"")), request.getLanguage()));
        */
                String hitResult = hit.getSourceAsString();

                String processedHitResult = hitResult
                        .replace(":\"{", ":{")
                        .replace("}\",", "},")
                        .replace("\\\"", "\"");
                results.add(processedHitResult);

            } catch (Exception e) {
                logger.error("ERR-1171: Could not parse log {}.", hit.getSourceAsString(), e);
            }

        }

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}
