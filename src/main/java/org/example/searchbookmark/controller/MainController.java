package org.example.searchbookmark.controller;

import jakarta.servlet.http.HttpSession;
import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.service.BookmarkService;
import org.example.searchbookmark.service.SearchService;
import org.example.searchbookmark.util.MyLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
//@RequestMapping("/") // 이런 경우는 생략해도 무방함
public class MainController {
    final private MyLogger logger = new MyLogger(this.getClass().getSimpleName());

    // 멤버변수
    private final SearchService searchService;
    private final BookmarkService bookmarkService;
    // 구별을 굳이 안해도 돼 -> 1:1 대응이 되니까

    // 생성자 주입 -> 의존성 주입을 한 타입은? SearchService
    public MainController(SearchService searchService, BookmarkService bookmarkService) {
        this.searchService = searchService;
        this.bookmarkService = bookmarkService;
    }

    @GetMapping // 클라이언트가 GET 방식으로 "/index" 같은 주소에 접근했을 때 실행되는 메서드
    public String index(Model model, // JSP로 데이터를 넘기기 위한 객체
                        
     // 요청 파라미터 중에 keyword라는 이름이 있을 경우, 그 값을 변수로 받음
     // 예: 사용자가 /index?keyword=고양이 라고 검색하면 keyword에 "고양이"가 들어감
     // required = false 라고 적었기 때문에 keyword가 없어도 에러가 나지 않음
    
    @RequestParam(value = "keyword",
            required = false) 
    String keyword, HttpSession session // 현재 로그인한 사용자나 방문자의 정보를 저장하는 세션 객체 (쿠키랑 연결됨)
    ) throws Exception { // "이 메서드 안에서 예외(Exception)가 발생할 수 있어요" 라고 컴파일러에게 알려주는 거
        logger.info(keyword);  // 로그에 사용자가 검색한 keyword를 출력 (서버 콘솔 확인용)
        if (keyword == null) { // keyword가 아예 없는 경우 (검색하지 않고 그냥 페이지 들어왔을 때)
            return "index";  // 검색 없이 그냥 index.jsp를 보여줌
        }
        List<KeywordSearch> result = searchService.searchByKeyword(keyword); // keyword가 있는 경우, 그 키워드로 검색을 수행함
        Map<String, KeywordSearch> map = new HashMap<>();
        for (KeywordSearch keywordSearch : result) {
            map.put(keywordSearch.uuid(), keywordSearch); // 검색 결과를 (uuid → 해당 결과) 형태의 Map으로 저장
        }
        // 검색 결과 Map을 세션에 저장해둠 (브라우저 닫기 전까지 유지됨)
        // 다음에 다시 접근할 때 DB를 다시 조회하지 않고 이걸 쓸 수 있음 (캐싱처럼)
        session.setAttribute("temp", map); 
         // 검색 결과 리스트를 JSP에 넘겨줌 (JSP에서 ${result}로 접근 가능)
        model.addAttribute("result", result);
        // 검색 결과를 포함한 index.jsp를 보여줌
        return "index";
    }

    @PostMapping("/bookmark") // 사용자가 북마크 버튼을 눌렀을 때 (POST 요청으로 "/bookmark"로 들어올 때 실행됨)
    public String bookmark(
        // 사용자가 북마크할 항목의 uuid를 요청 파라미터로 받음 (예: uuid=abc123)
        @RequestParam("uuid") 
        String uuid, 
        Model model, // JSP로 값 넘길 때 사용할 수 있는 모델 객체 
        HttpSession session // 사용자 세션에서 캐시된 검색 결과를 꺼내기 위해 사용
        ) throws Exception {
         // 세션에 저장된 검색 결과 map 꺼내기 (uuid → KeywordSearch 객체)
         Map<String, KeywordSearch> temp = (HashMap<String, KeywordSearch>) session.getAttribute("temp");
        // 해당 uuid의 검색 결과를 기반으로 북마크 생성 (서비스에서 처리)
        String resultID = bookmarkService.createBookmark(temp.get(uuid));
        // 북마크가 성공적으로 생성되면, 그 결과 uuid로 이동 (리디렉션)
        return "redirect:/%s".formatted(resultID); 
    }

    // 주소창에서 "/{uuid}"로 접근하면 실행됨
    // 예: 사용자가 "/3f2f34af"로 접근하면 uuid 값이 "3f2f34af"로 들어옴
    @GetMapping("/{uuid}")
    public String search(
        // URL 경로의 일부로부터 uuid 값을 받아옴 (→ 경로 변수)
        @PathVariable("uuid") 
        String uuid, 
        // 결과를 JSP에 넘기기 위한 모델
        Model model
        ) throws Exception {
        // 해당 uuid에 해당하는 북마크 하나를 서비스에서 불러와서 모델에 담음
        model.addAttribute("bookmark", bookmarkService.readOneBookmark(uuid));
        // bookmark.jsp 파일을 보여줌 (검색 결과 상세 페이지 같은 느낌)
        return "bookmark";
    }
}
