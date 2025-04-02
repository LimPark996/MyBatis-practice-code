## 1. Spring MVC란?

> Spring MVC는 Java 웹 개발을 위한 프레임워크입니다. 사용자 요청을 받아 적절한 컨트롤러로 전달하고, 그 결과를 JSP 같은 뷰(View)로 보여주는 구조입니다.

### 🌱 MVC 패턴이란?
MVC는 웹 애플리케이션을 역할별로 나눠 구조화하는 방법입니다.

| 구성 요소 | 역할 |
|------------|------|
| **Model** | 데이터, 비즈니스 로직, DB에서 가져온 정보 |
| **View**  | 사용자에게 보여지는 화면 (예: JSP) |
| **Controller** | 사용자의 요청을 받아서 처리하고 결과를 뷰에 넘김 |

### ✅ Spring MVC 요청 흐름
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

## 2. 기존 서블릿 방식 vs Spring MVC 방식

### ✅ 목표: 간단한 글쓰기 + 목록 보기 기능 만들기
- `/post/list` : 글 목록 보기
- `/post/write` : 글 작성 폼, 글 저장

---

### 💥 기존 방식: 순수 서블릿 방식

#### 🔧 구조 예시
```
webapp/
├── servlet/
│   ├── PostListServlet.java
│   └── PostWriteServlet.java
├── repository/PostRepository.java
├── model/Post.java
└── views/list.jsp, write.jsp
```

#### 📌 코드 예시
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

#### ❌ 문제점
- 서블릿마다 doGet(), doPost() 직접 구분 필요
- 매 요청마다 파라미터 추출, 응답 전송 직접 구현
- 뷰 경로 수동 지정
- 객체 관리 어려움 (DI 안 됨)
- 테스트, 확장성 매우 떨어짐

---

### ✅ Spring MVC 방식으로 개선

#### 🌿 구조 예시
```
src/
├── controller/PostController.java
├── repository/PostRepository.java
├── model/Post.java
└── views/list.jsp, write.jsp
```

#### 📌 코드 예시
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

#### ✅ 개선점
| 항목 | 기존 서블릿 방식 | Spring MVC 방식 |
|------|------------------|------------------|
| 요청 처리 | HttpServlet 상속, URL 직접 매핑 | @Controller + @RequestMapping 자동 처리 |
| 파라미터 받기 | getParameter() | @RequestParam으로 자동 매핑 |
| 데이터 전달 | request.setAttribute() | model.addAttribute() |
| 뷰 연결 | requestDispatcher.forward() | return "뷰 이름" (ViewResolver가 처리) |
| 의존성 주입 | new 직접 생성 | 생성자 주입으로 자동 연결 (DI) |

---

## 3. Spring MVC 필수 구성 파일과 역할

| 설정 파일 | 설명 |
|------------|------|
| AppConfig.java | 어떤 클래스(@Controller, @Service 등)를 Bean으로 등록할지 결정 (컴포넌트 스캔) |
| WebAppInitializer.java | 프로젝트 시작 시 DispatcherServlet을 등록 (web.xml 대체) |
| WebConfig.java | 뷰(JSP) 위치 설정, 정적 리소스(css/js) 설정 |
| MyBatisConfig.java | DB 연결, mybatis-config.xml 로드, SqlSessionFactory 생성 |

---

### ✅ AppConfig.java
```java
@Configuration
@ComponentScan("org.example")
public class AppConfig {}
```
- Bean으로 관리할 컴포넌트(@Controller, @Service 등)를 자동 탐색

### ✅ WebAppInitializer.java
```java
public class WebAppInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext ctx) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        DispatcherServlet ds = new DispatcherServlet(context);
        ctx.addServlet("dispatcherServlet", ds).addMapping("/");
    }
}
```
- 프로젝트 시작 시 DispatcherServlet이 모든 요청을 가로채게 등록

### ✅ WebConfig.java
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setPrefix("/WEB-INF/views/");
        vr.setSuffix(".jsp");
        return vr;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/asset/**")
                .addResourceLocations("/asset/");
    }
}
```
- ViewResolver: 컨트롤러에서 return "list" → 실제로 /WEB-INF/views/list.jsp로 연결
- 정적 파일 요청은 DispatcherServlet을 거치지 않고 직접 처리

---

### ✅ MyBatisConfig.java + BookmarkMapper.xml 예시
```java
// MyBatisConfig.java
InputStream inputStream = ... // mybatis-config.xml
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
```
```xml
<!-- BookmarkMapper.xml -->
<insert id="insertBookmark" parameterType="KeywordSearch">
    INSERT INTO ...
</insert>

<resultMap id="BookmarkMap" type="KeywordSearch">
    <constructor>
        <arg column="bookmark_id" name="uuid" />
        ...
    </constructor>
</resultMap>

<select id="getOneBookmark" resultMap="BookmarkMap">
    SELECT * FROM ... WHERE bookmark_id = #{uuid}
</select>
```
- `resultMap`은 SELECT 결과를 record 등 생성자 기반 객체로 매핑할 때 필요
- INSERT/UPDATE는 resultMap 필요 없음

---

## 🔚 발표 마무리 요약

✅ Spring MVC는 서블릿보다 구조적이고 확장성이 뛰어납니다.  
✅ DispatcherServlet이 요청의 흐름을 제어하고, 구성 파일들이 역할별로 정리되어 있어요.  
✅ 뷰 연결, 파라미터 처리, 객체 주입 등 반복 작업이 간단해지고 유지보수성이 향상됩니다.

💡 **한 줄 요약**
> Spring MVC는 Java 웹개발에서 가장 많이 쓰이는 구조화된 표준이며,  
> 컨트롤러 중심으로 클린한 구조를 제공해요!
