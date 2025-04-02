### 1. Spring MVC란?

> Spring MVC는 Java 웹 개발을 위한 프레임워크입니다. 사용자 요청을 받아 적절한 컨트롤러로 전달하고, 그 결과를 JSP 같은 뷰(View)로 보여주는 구조입니다.

**🌱 MVC 패턴이란?**

MVC는 웹 애플리케이션을 역할별로 나눠 구조화하는 방법입니다.

| 구성 요소 | 역할 |
|------------|------|
| **Model** | 데이터, 비즈니스 로직, DB에서 가져온 정보 |
| **View**  | 사용자에게 보여지는 화면 (예: JSP) |
| **Controller** | 사용자의 요청을 받아서 처리하고 결과를 뷰에 넘김 |

**✅ Spring MVC 요청 흐름**

```
1. 사용자가 /search 요청을 보냄
2. DispatcherServlet이 요청을 가로채고
3. 해당 요청을 처리할 Controller로 전달
4. Controller는 Service → DB 접근
5. 처리 결과를 Model에 담고
6. ViewResolver가 JSP 파일 위치를 결정
7. 최종적으로 JSP가 사용자에게 보여짐
```

---

### 2. 기존 서블릿 방식 vs Spring MVC 방식

**✅ 목표: 간단한 글쓰기 + 목록 보기 기능 만들기**

- `/post/list` : 글 목록 보기
- `/post/write` : 글 작성 폼, 글 저장

---

**💥 기존 방식: 순수 서블릿 방식**

**🔧 구조 예시**

```
webapp/
├── servlet/
│   ├── PostListServlet.java
│   └── PostWriteServlet.java
├── repository/PostRepository.java
├── model/Post.java
└── views/list.jsp, write.jsp
```

**📌 코드 예시**

```java
@WebServlet("/post/write")
public class PostWriteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        PostRepository.save(new Post(title, content));
        res.sendRedirect("/post/list");
    }
}
```
```java
@WebServlet("/post/list")
public class PostListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("posts", PostRepository.findAll());
        req.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(req, res);
    }
}
```

**❌ 문제점**

- 서블릿마다 doGet(), doPost() 직접 구분 필요
- 매 요청마다 파라미터 추출, 응답 전송 직접 구현
- 뷰 경로 수동 지정
- 객체 관리 어려움 (DI 안 됨)
- 테스트, 확장성 매우 떨어짐

---

**✅ Spring MVC 방식으로 개선**

**🌿 구조 예시**
```
src/
├── controller/PostController.java
├── repository/PostRepository.java
├── model/Post.java
└── views/list.jsp, write.jsp
```

**📌 코드 예시**
```java
@Controller
@RequestMapping("/post")
public class PostController {
    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "list";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "write";
    }

    @PostMapping("/write")
    public String write(@RequestParam String title, @RequestParam String content) {
        postRepository.save(new Post(title, content));
        return "redirect:/post/list";
    }
}
```

**✅ 개선점**

| 항목 | 기존 서블릿 방식 | Spring MVC 방식 |
|------|------------------|------------------|
| 요청 처리 | HttpServlet 상속, URL 직접 매핑 | @Controller + @RequestMapping 자동 처리 |
| 파라미터 받기 | getParameter() | @RequestParam으로 자동 매핑 |
| 데이터 전달 | request.setAttribute() | model.addAttribute() |
| 뷰 연결 | requestDispatcher.forward() | return "뷰 이름" (ViewResolver가 처리) |
| 의존성 주입 | new 직접 생성 | 생성자 주입으로 자동 연결 (DI) |

---

### 3. Spring MVC 필수 구성 파일과 역할

| 설정 파일 | 설명 |
|------------|------|
| AppConfig.java | 어떤 클래스(@Controller, @Service 등)를 Bean으로 등록할지 결정 (컴포넌트 스캔) |
| WebAppInitializer.java | 프로젝트 시작 시 DispatcherServlet을 등록 (web.xml 대체) |
| WebConfig.java | 뷰(JSP) 위치 설정, 정적 리소스(css/js) 설정 |
| MyBatisConfig.java | DB 연결, mybatis-config.xml 로드, SqlSessionFactory 생성 |

**Bean(빈) = 스프링이 자동으로 new 해주는 객체**

왜 굳이 Bean으로 만들어요?

이유 1. 자동 연결(DI, 의존성 주입)

```
SearchService searchService = new NaverSearchService();
MainController controller = new MainController(searchService);
```

searchService를 내가 new 하지(객체를 생성하지) 않았는데도! 스프링이 알아서 넣어줘요 → 왜? 그 객체가 Bean이니까요.

이유 2. 프로젝트 전반에서 공유 가능

스프링은 Bean을 보통 싱글톤(singleton) 으로 관리해요. 즉, NaverSearchService라는 Bean을 하나만 만들고 모든 곳에서 그 하나를 공유하게 해요.

---

**✅ AppConfig.java**

```java
package org.example.searchbookmark.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// 스프링이 어떤 클래스를 빈(Bean)으로 관리할지 알려주는 지도

@Configuration
// 본인의 패키지명 중에 가장 좁히는 범위
@ComponentScan(basePackages = "org.example.searchbookmark")
//"Spring아, org.example.searchbookmark 패키지와 그 하위에 있는 클래스들 중에서
//@Component, @Service, @Repository, @Controller 같은 어노테이션 붙은 애들 자동으로 찾아서 Bean으로 등록해줘!"
public class AppConfig {
}
```
- Bean으로 관리할 컴포넌트(@Controller, @Service 등)를 자동 탐색

**✅ WebAppInitializer.java**

```java
package org.example.searchbookmark.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

WebAppInitializer는 톰캣이 켜질 때
스프링 컨테이너를 생성하고,
DispatcherServlet을 등록해서,
웹 요청이 컨트롤러로 흐르게 만드는 첫 번째 설정 클래스예요!

AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
context.register(org.example.searchbookmark.config.AppConfig.class);의 동작 흐름

```
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
```
- 프로젝트 시작 시 DispatcherServlet이 모든 요청을 가로채게 등록

**✅ WebConfig.java**

```java
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

설정클래스	핵심 기능
AppConfig	"스프링이 어떤 클래스들을 관리할지 알려주는 지도" (Bean 등록용)
MyBatisConfig	"DB 설정 + MyBatis 연결" (SQL 실행 가능하게 해줌)
WebAppInitializer	"스프링 웹앱을 시작할 때 처음 실행되는 곳" (DispatcherServlet 등록)
WebConfig   	"뷰(JSP) 설정, 정적 리소스(css, js 등) 설정" → 웹 화면을 제대로 보이게 해주는 설정

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
- ViewResolver: 컨트롤러에서 return "list" → 실제로 /WEB-INF/views/list.jsp로 연결
- 정적 파일 요청은 DispatcherServlet을 거치지 않고 직접 처리

---

**✅ MyBatisConfig.java + BookmarkMapper.xml 예시**

**MyBatis란?**

자바 객체와 SQL을 연결해주는 프레임워크

즉, DB에 직접 SQL을 날리되, 자바 코드에서 SQL을 간편하게 실행하고, 결과를 객체로 바꿔주는 도구예요.

    [자바 코드] ⇄ [MyBatis Mapper] ⇄ [SQL] ⇄ [DB]

이 클래스는:

SqlSessionFactory라는 MyBatis 핵심 객체를 생성하고 환경변수(.env 파일)를 통해 DB 정보를 불러오고

mybatis-config.xml 설정 파일을 로드해서 MyBatis와 DB를 연결하는 설정을 초기화하는 역할을 합니다.

즉, MyBatis를 쓰기 위한 기반 설정을 다 모아둔 클래스예요.

[처음] MyBatisConfig.getSqlSessionFactory() 호출됨

   ↓

[1] static 블록이 실행됨
     - .env 로드
     - DB 설정 등록
     - mybatis-config.xml 읽음
     - SqlSessionFactory 생성

   ↓

[2] SqlSessionFactory를 외부로 제공함

   ↓

[3] 다른 코드에서 SqlSession을 열고 SQL 실행
     - Mapper(XML) 안의 SQL이 실행됨
     - 결과는 자바 객체로 매핑됨

SqlSession: MyBatis가 DB에 SQL을 실행할 수 있게 해주는 객체예요.

```java
/ MyBatis 설정을 자바 코드로 구성한 클래스
public class MyBatisConfig {

    // MyBatis의 핵심 객체: SQL 세션을 생성하는 팩토리
    private static final SqlSessionFactory sqlSessionFactory;

    // 로그 찍기용 Logger
    private static final Logger logger = Logger.getLogger(MyBatisConfig.class.getName());

    // static 블록: 클래스가 처음 로딩될 때 한 번 실행됨
    static {

        // .env 파일을 불러와 환경변수 설정 (DB 정보 등)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Properties 객체에 환경변수들을 저장
        Properties properties = new Properties();
        properties.setProperty("DB_DRIVER", dotenv.get("DB_DRIVER"));       // DB 드라이버 클래스명
        properties.setProperty("DB_URL", dotenv.get("DB_URL"));             // DB 주소
        properties.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));   // DB 사용자 이름
        properties.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));   // DB 비밀번호

        // 환경변수 로딩 로그 출력
        logger.info("MyBatisConfig: " + properties);

        // MyBatis 설정 파일 경로
        String resource = "mybatis-config.xml";

        try (
            // 설정 파일을 InputStream으로 로드
            InputStream inputStream = MyBatisConfig.class.getClassLoader().getResourceAsStream(resource)
        ) {
            // 파일이 잘 로드됐는지 로그 출력
            logger.info("%s".formatted(inputStream));
            logger.info(String.valueOf(MyBatisConfig.class.getClassLoader().getResource(resource)));

            // SqlSessionFactory를 생성 (설정 파일 + DB 접속 정보 기반)
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);

        } catch (IOException e) {
            // 설정 파일을 못 읽었을 경우 예외 발생
            throw new RuntimeException(e);
        }

        logger.info("config 완료");
    }

    // 외부에서 SqlSessionFactory를 가져올 수 있도록 제공
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
```
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!-- MyBatis 전용 문서라는 걸 알려주는 선언 (정해진 틀이라고 생각하면 돼요) -->
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- 이 mapper는 어떤 자바 인터페이스와 연결되는지 알려줘요 -->
<!-- namespace는 BookmarkMapper.java의 전체 경로랑 완전히 똑같아야 함 -->
<mapper namespace="org.example.searchbookmark.model.mapper.BookmarkMapper">

    <!-- 가장 마지막으로 저장된 북마크의 ID 한 개 가져오기 -->
    <!--id: 자바에서 mapper.checkLastID()로 연결-->
    <!--resultType: 결과가 문자열 하나니까 String으로 받겠다는 의미-->
    <select id="checkLastID" resultType="String">
        SELECT bookmark_id FROM search_result_bookmark
        ORDER BY created_at DESC LIMIT 1
    </select>
    
    <!-- 북마크 하나를 DB에 저장하는 SQL -->
    <!--id: 자바에서 mapper.insertBookmark(keywordSearch)로 실행됨-->
    <!--parameterType: 자바에서 넘길 객체의 타입. KeywordSearch 필드랑 이름 맞춰야 함-->
    <!--#{title}: 자바 객체 안에 있는 필드값 꺼내 쓰는 문법-->
    <insert id="insertBookmark" parameterType="org.example.searchbookmark.model.vo.KeywordSearch">
        INSERT INTO search_result_bookmark (title, link, description, date) values (#{title}, #{link}, #{description}, #{date})
    </insert>
    
    <!-- SELECT 결과를 자바 객체(KeywordSearch)로 만들기 위한 규칙 -->
    <resultMap id="BookmarkMap" type="org.example.searchbookmark.model.vo.KeywordSearch">
        <constructor>
             <!-- 생성자에 값을 어떻게 넣을지 하나하나 지정 -->
            <arg column="bookmark_id" javaType="String" name="uuid" />
            <arg column="title" javaType="String" name="title" />
            <arg column="link" javaType="String" name="link" />
            <arg column="description" javaType="String" name="description" />
            <arg column="date" javaType="String" name="date" />
            <arg column="created_at" javaType="String" name="createdAt" />
        </constructor>
    </resultMap>
    
    <!-- 북마크 하나를 uuid로 조회해서 KeywordSearch 객체로 변환 -->
    <select id="getOneBookmark" resultMap="BookmarkMap" parameterType="String">
        SELECT * FROM search_result_bookmark
        WHERE bookmark_id = #{uuid}
    </select>
</mapper>

❗ resultMap은 SELECT 할 때만 필요해요.
❌ INSERT, UPDATE, DELETE는 객체를 만들 필요가 없기 때문에 resultMap이 필요 없어요.
🧩 SELECT는 결과를 "자바 객체"로 만들어서 돌려줘야 하기 때문이에요.

✅ 반면, INSERT/UPDATE/DELETE는?
이건 DB에 값을 넣는 것이죠.
자바 객체 → SQL로 파라미터만 넘기면 끝이에요.
객체를 다시 만들 필요가 없어요.
그래서 resultMap이 필요 ❌
```
- `resultMap`은 SELECT 결과를 record 등 생성자 기반 객체로 매핑할 때 필요
- INSERT/UPDATE는 resultMap 필요 없음
