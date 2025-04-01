package org.example.searchbookmark.controller;

import org.example.searchbookmark.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
//@RequestMapping("/") // 이런 경우는 생략해도 무방함
public class MainController {
    // 멤버변수
    private final SearchService searchService;
    // 구별을 굳이 안해도 돼 -> 1:1 대응이 되니까

    // 생성자 주입
    public MainController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public String index() {
        searchService.searchByKeyword("4월은 너의 거짓말");
        return "index";
    }
}
