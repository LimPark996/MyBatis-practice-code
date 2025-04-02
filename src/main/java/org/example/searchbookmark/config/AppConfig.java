package org.example.searchbookmark.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// 스프링이 어떤 클래스를 빈(Bean)으로 관리할지 알려주는 지도

@Configuration
// 본인의 패키지명 중에 가장 좁히는 범위
@ComponentScan(basePackages = "org.example.searchbookmark")
```
"Spring아, org.example.searchbookmark 패키지와 그 하위에 있는 클래스들 중에서
@Component, @Service, @Repository, @Controller 같은 어노테이션 붙은 애들 자동으로 찾아서 Bean으로 등록해줘!"
```
public class AppConfig {
}
    ```
    Bean(빈) = 스프링이 자동으로 new 해주는 객체
    🎯 왜 굳이 Bean으로 만들어?
        이유 1. 자동 연결(DI, 의존성 주입)
        public MainController(SearchService searchService) {
            this.searchService = searchService;
        }
        searchService를 내가 new 하지 않았는데도!
        스프링이 알아서 넣어줘요 → 왜? 그 객체가 Bean이니까요.
        이유 2. 프로젝트 전반에서 공유 가능
        스프링은 Bean을 보통 싱글톤(singleton) 으로 관리해요.
        즉, NaverSearchService라는 Bean을 하나만 만들고
        모든 곳에서 그 하나를 공유하게 해요.
    ```
