package org.example.searchbookmark.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperMixin {
    ObjectMapper objectMapper = new ObjectMapper();
}

// 이 ObjectMapperMixin은
// ObjectMapper를 static 상수로 미리 만들어두고,
// 여러 클래스에서 재사용하기 위한 인터페이스예요.
