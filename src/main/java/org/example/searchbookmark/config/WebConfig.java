package org.example.searchbookmark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

// WebConfig는 **“Spring MVC 기능을 사용하기 위한 설정”**이에요.
// 📌 즉, JSP 뷰 설정, 정적 파일(css, js, 이미지 등) 위치, 이런 웹 쪽 전용 설정은
// AppConfig가 아닌 WebConfig에서 따로 해줘야 해요.
// AppConfig: 서비스, DB 설정
// WebConfig: 웹 MVC 관련 설정

```
설정클래스	핵심 기능
AppConfig	"스프링이 어떤 클래스들을 관리할지 알려주는 지도" (Bean 등록용)
MyBatisConfig	"DB 설정 + MyBatis 연결" (SQL 실행 가능하게 해줌)
WebAppInitializer	"스프링 웹앱을 시작할 때 처음 실행되는 곳" (DispatcherServlet 등록)
WebConfig   	"뷰(JSP) 설정, 정적 리소스(css, js 등) 설정" → 웹 화면을 제대로 보이게 해주는 설정
```

@Configuration
@EnableWebMvc // Spring MVC 기능들을 사용하겠다고 선언하는 어노테이션. DispatcherServlet이 요청을 받아서 컨트롤러로 넘기고, 뷰를 렌더링하는 과정을 작동시켜줌
public class WebConfig implements WebMvcConfigurer {

    @Bean
    // 컨트롤러에서 return "index"라고 했을 때
    // → 실제로는 /WEB-INF/views/index.jsp를 찾아가게 해주는 설정이에요.
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setOrder(1);
        return viewResolver;
    }

    // /asset/style.css 같은 요청이 들어오면
    // 실제로 /webapp/asset/style.css 파일을 찾아보도록 설정해주는 부분이에요.
    // 즉, 정적 파일이 컨트롤러를 거치지 않고 바로 응답될 수 있도록 처리해줘요.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/asset/**")
                .addResourceLocations("/asset/"); // 또는 "classpath:/static/asset/"
    }
}

```
정적 파일을 컨트롤러가 안 거치게 하는 이유

1. 컨트롤러는 동적인 요청 처리용
HTML, 검색 결과, 게시글 목록처럼 내용이 매번 바뀌는 "동적 데이터"를 처리해요.

2. CSS, JS, 이미지 같은 정적 자원은
내용이 안 바뀌고, 그냥 있는 그대로 내려주면 끝

3. 그런데 이걸 컨트롤러가 처리하게 하면?
하나의 style.css 내려주는데도
DispatcherServlet → HandlerMapping → Controller → ViewResolver
이 모든 단계를 거치게 돼요

👉 즉, 불필요하게 무거운 경로를 타게 되고
👉 성능이 떨어져요 (처리 시간도 느려지고, 서버 부담도 커짐)
```
