package org.example.searchbookmark.service;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.util.MyLogger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 등록했는데... SearchService이자 NaverSearchService
// 등록된 타입은? <Naver>SearchService
public class NaverSearchService implements SearchService
{
    private final MyLogger logger = new MyLogger(this.getClass().getName());

    @Override
    public List<KeywordSearch> searchByKeyword(String keyword) {
        logger.info("searchByKeyword keyword: %s".formatted(keyword));
        return List.of();
    }
}
