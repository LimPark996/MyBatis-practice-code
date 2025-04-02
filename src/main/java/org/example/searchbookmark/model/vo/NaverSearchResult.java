package org.example.searchbookmark.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

//네이버 검색 API로부터 받은 JSON 데이터를 자바 객체로 변환하기 위한 구조

// JSON을 자바 객체로 변환할 때, JSON에 정의되지 않은(=우리가 선언하지 않은) 속성이 있더라도 무시하라는 뜻
// 예: JSON에 "lastBuildDate" 같은 다른 필드가 있어도 오류 없이 무시
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverSearchResult(List<Item> items) { // 전체 검색 결과를 담는 바깥쪽 record
    // 내부의 각 검색 결과 하나하나 (블로그 글 하나) 를 표현하는 record
    // 역시 우리가 지정하지 않은 필드는 무시함 (예: bloggername 같은 필드가 있어도 무시)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
        String title,       // 블로그 글 제목
        String link,        // 글 링크
        String description, // 설명 또는 요약
        String postdate     // 작성일자 (yyyyMMdd)
    ) {}
}
