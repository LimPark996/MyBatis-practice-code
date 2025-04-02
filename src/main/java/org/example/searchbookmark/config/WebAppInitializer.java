package org.example.searchbookmark.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

```
WebAppInitializer는 톰캣이 켜질 때
스프링 컨테이너를 생성하고,
DispatcherServlet을 등록해서,
웹 요청이 컨트롤러로 흐르게 만드는 첫 번째 설정 클래스예요!
```

```
AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
context.register(org.example.searchbookmark.config.AppConfig.class);의 동작 흐름

[WebAppInitializer] ← 프로젝트 진입점
     ↓
[AnnotationConfigWebApplicationContext] ← 스프링 환경 만들기
     ↓
.register(AppConfig.class) ← 스프링 설정 파일 알려주기
     ↓
@ComponentScan(...) ← Bean 등록할 범위 설정됨
     ↓
@Service, @Controller, ... → Bean 등록 완료
```

// 웹 프로젝트가 시작될 때 처음 실행되는 클래스
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        // 스프링 설정 파일을 읽기 위한 준비
        // "Spring아, 이 context라는 스프링 환경을 만들 건데 AppConfig.java 안에 정의된 설정들을 읽고 시작해줘!"
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        // AppConfig.java 안에 @ComponentScan 같은 설정을 읽겠다고 등록
        // AppConfig는 단순히 **"스프링 설정이 담긴 자바 파일"**일 뿐이에요. 하지만 스프링은 우리가 뭘 등록하라고 말하지 않으면 아무 것도 실행하지 않아요.
        // 이걸 명시적으로 해줘야
        // 👉 "아~ AppConfig 안의 @ComponentScan 써서,
        // 👉 @Service, @Controller 붙은 클래스들을 Bean으로 등록하겠구나~"
        // 라고 이해하게 되는 거예요.
        context.register(org.example.searchbookmark.config.AppConfig.class);

        // 웹 요청을 받아서 컨트롤러로 보내주는 DispatcherServlet 만들기
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);

        // 만든 DispatcherServlet을 톰캣에 등록함
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcherServlet", dispatcherServlet);

        // 서버 켜질 때 제일 먼저 이 서블릿을 실행하게 함
        registration.setLoadOnStartup(1);

        // 모든 주소 요청("/")은 이 서블릿이 처리하게 설정
        registration.addMapping("/");
    }
}
