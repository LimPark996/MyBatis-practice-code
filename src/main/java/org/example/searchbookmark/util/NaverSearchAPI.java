package org.example.searchbookmark.util;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.model.vo.NaverSearchParam;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NaverSearchAPI implements DotenvMixin, ObjectMapperMixin {
    private final MyLogger logger = new MyLogger(this.getClass().getSimpleName());
    private final HttpClient httpClient = HttpClient.newHttpClient();

    List<KeywordSearch> callAPI(NaverSearchParam param) throws Exception {
        // https://developers.naver.com/main/
        HttpRequest request = HttpRequest.newBuilder().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        logger.info(responseBody);
        return List.of();
     }
}
