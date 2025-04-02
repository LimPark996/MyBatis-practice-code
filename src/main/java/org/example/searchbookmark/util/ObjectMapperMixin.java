package org.example.searchbookmark.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperMixin {
    ObjectMapper objectMapper = new ObjectMapper();
}

// 이 ObjectMapperMixin은
// ObjectMapper를 static 상수로 미리 만들어두고,
// 여러 클래스에서 재사용하기 위한 인터페이스예요.

// ObjectMapper는 Jackson 라이브러리에서 제공하는 클래스로,
// Java 객체를 JSON으로 바꾸거나(JSON 직렬화),
// JSON 문자열을 Java 객체로 바꾸는 것(JSON 역직렬화) 을 도와주는 도구예요.

// Jackson 라이브러리는 스프링에서 기본으로 들어 있는 JSON 처리 도구예요.
