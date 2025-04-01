package org.example.searchbookmark.controller;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.service.SearchService;
import org.example.searchbookmark.util.MyLogger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@Controller
//@RequestMapping("/") // 이런 경우는 생략해도 무방함
public class MainController {
    final private MyLogger logger = new MyLogger(this.getClass().getSimpleName());

    // 멤버변수
    private final SearchService searchService;
    // 구별을 굳이 안해도 돼 -> 1:1 대응이 되니까

    // 생성자 주입 -> 의존성 주입을 한 타입은? SearchService
    public MainController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public String index(Model model,
//                        @RequestParam("keyword") String keyword
                        // required = true 가 숨겨져 있어
                        @RequestParam(value = "keyword",
//                                defaultValue = "야구 순위",
                                required = false) String keyword
    ) throws Exception {
        logger.info(keyword);
        if (keyword == null) {
            return "index";
        }
        List<KeywordSearch> result = searchService.searchByKeyword(keyword);
        model.addAttribute("result", result);
        return "index";
    }

    @PostMapping("/bookmark")
    public String bookmark(@ModelAttribute KeywordSearch keywordSearch, Model model) throws Exception {
        return "redirect:/"; // servlet으로 보내기
    }
}
