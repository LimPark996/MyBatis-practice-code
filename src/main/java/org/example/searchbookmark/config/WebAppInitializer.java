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

// 웹 프로젝트가 시작될 때 처음 실행되는 클래스
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        // 스프링 설정 파일을 읽기 위한 준비
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        // AppConfig.java 안에 @ComponentScan 같은 설정을 읽겠다고 등록
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
