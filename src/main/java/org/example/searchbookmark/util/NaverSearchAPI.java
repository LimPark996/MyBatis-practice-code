package org.example.searchbookmark.util;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.model.vo.NaverSearchParam;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NaverSearchAPI implements DotenvMixin, ObjectMapperMixin {
    private final MyLogger logger = new MyLogger(this.getClass().getSimpleName());
    private final HttpClient httpClient = HttpClient.newHttpClient();

    List<KeywordSearch> callAPI(NaverSearchParam param) throws Exception {
        // https://developers.naver.com/main/
        // https://developers.naver.com/docs/serviceapi/search/blog/blog.md
        String url = "https://openapi.naver.com/v1/search/blog.json";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s?query=%s".formatted(url, param.query()))) // URL로부터 호출을 시도할 URI를 생성
                .header("X-Naver-Client-Id", dotenv.get("NAVER_CLIENT_ID"))
                .header("X-Naver-Client-Secret", dotenv.get("NAVER_CLIENT_SECRET"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        logger.info(responseBody);
        return List.of();
     }
}
