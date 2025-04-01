package org.example.searchbookmark.service;

import org.example.searchbookmark.model.vo.KeywordSearch;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service // Spring Container 에 Component 로 등록
public interface SearchService {
    List<KeywordSearch> searchByKeyword(String keyword);
}
