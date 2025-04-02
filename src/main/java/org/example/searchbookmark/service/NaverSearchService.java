package org.example.searchbookmark.service;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.model.vo.NaverSearchParam;
import org.example.searchbookmark.util.MyLogger;
import org.example.searchbookmark.util.NaverSearchAPI;
import org.springframework.stereotype.Service;

import java.util.List;

// 이 클래스는 Service 역할을 함을 Spring에게 알려줌
// → 자동으로 Bean으로 등록돼서 Controller에 주입 가능
@Service 
public class NaverSearchService implements SearchService // SearchService 구현
{
    private final MyLogger logger = new MyLogger(this.getClass().getName());
    
    // 실제로 네이버 API를 호출하는 객체
    private final NaverSearchAPI naverSearchAPI;

    public NaverSearchService(NaverSearchAPI naverSearchAPI) {
        this.naverSearchAPI = naverSearchAPI;
    }
    // SearchService에서 정의한 메서드를 실제로 구현
    // 검색어(keyword)를 받아서 네이버 API에 요청하고 결과를 반환함
    @Override
    public List<KeywordSearch> searchByKeyword(String keyword) throws Exception {
 
        logger.info("searchByKeyword keyword: %s".formatted(keyword));
        
         // 네이버 API에 요청을 보내고 결과(List<KeywordSearch>)를 받아서 그대로 반환
        return naverSearchAPI.callAPI(new NaverSearchParam(keyword));
    }
}
