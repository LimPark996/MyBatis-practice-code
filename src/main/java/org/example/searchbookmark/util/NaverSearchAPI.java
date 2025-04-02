@Component // 스프링 컴포넌트로 등록 (서비스에서 사용할 수 있게)
public class NaverSearchAPI implements DotenvMixin, ObjectMapperMixin {

    private static final Log log = LogFactory.getLog(NaverSearchAPI.class);
    private final MyLogger logger = new MyLogger(this.getClass().getSimpleName());
    private final HttpClient httpClient = HttpClient.newHttpClient(); // 자바 11 내장 HTTP 클라이언트

    // 외부에서 검색을 요청하면 이 메서드가 호출됨
    public List<KeywordSearch> callAPI(NaverSearchParam param) throws Exception {
        // 네이버 블로그 검색 API 주소
        String url = "https://openapi.naver.com/v1/search/blog.json";

        // 검색어 쿼리 파라미터를 URL에 안전하게 붙이기 위해 인코딩
        // 예: "강아지 사료" → "강아지%20사료"
        String query = URLEncoder.encode(param.query(), StandardCharsets.UTF_8);

        // HTTP 요청을 구성 (GET 방식)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s?query=%s".formatted(url, query))) // URL에 쿼리 붙임
                .header("X-Naver-Client-Id", dotenv.get("NAVER_CLIENT_ID"))       // 인증용 헤더 1
                .header("X-Naver-Client-Secret", dotenv.get("NAVER_CLIENT_SECRET")) // 인증용 헤더 2
                .build();

        // 요청을 실제로 보냄, 응답은 문자열 형태로 받음
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 응답 본문 (JSON 문자열)
        String responseBody = response.body();

        // JSON 문자열을 NaverSearchResult 객체로 변환
        NaverSearchResult naverSearchResult = objectMapper.readValue(responseBody, NaverSearchResult.class);

        // 결과 리스트를 우리가 쓰는 형태(KeywordSearch)로 바꿔서 리턴
        return naverSearchResult.items()
                .stream().map(item -> new KeywordSearch(
                        UUID.randomUUID().toString(), // uuid 임의 생성
                        item.title(),
                        item.link(),
                        item.description(),
                        item.postdate()
                ))
                .toList();
     }
}

```
[NaverSearchService]      ← 검색 요청
        ↓
[NaverSearchAPI]          ← 외부 요청 담당 (HttpClient로 Naver API 호출)
        ↓
[HttpRequest]             ← 요청 만들기 (URL, 헤더, 파라미터)
        ↓
[NaverSearchResult]       ← JSON 결과 → 자바 객체로 변환 (ObjectMapper)
        ↓
[List<KeywordSearch>]     ← 결과 가공해서 리턴
```
