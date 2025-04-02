### 🌱 Spring MVC란?

> **Spring MVC는 웹 요청을 받아서 → 컨트롤러에 전달하고 → 뷰(JSP 등)를 보여주는 웹 프레임워크**예요.

"MVC"는 아래 세 가지로 구성돼요:

| 구성요소 | 설명 |
|----------|------|
| Model (M) | 데이터, DB에서 가져온 정보 (예: 책 목록, 사용자 정보 등) |
| View (V) | 화면, JSP 같은 템플릿 파일 |
| Controller (C) | 사용자의 요청을 받아서 Model과 View를 연결해주는 중간 다리 |

---

**✅ 전체 흐름 한 눈에 보기**

```plaintext
[사용자] ➝ /book 요청
    ↓
[DispatcherServlet] ← Spring MVC의 핵심 서블릿
    ↓
[Controller] → 어떤 로직 실행 (예: 책 목록 가져오기)
    ↓
[Model] → DB 데이터 가져옴 (ex. MyBatis로)
    ↓
[ViewResolver] → "book.jsp" 찾기
    ↓
[JSP View] → 화면에 보여줌
```

---

### 🧩 구성 요소별 역할 + 어디서 설정되는지

| 구성요소 | 역할 | 어떤 파일에서 설정됨 |
|----------|------|--------------------|
| **AppConfig** | 스프링이 어떤 클래스(@Service, @Controller 등)를 Bean으로 등록할지 알려줌 | `@ComponentScan` 있음 |
| **WebAppInitializer** | 웹 프로젝트 시작할 때 스프링을 연결해주는 역할 (`web.xml` 대체) | DispatcherServlet 등록 |
| **WebConfig** | 뷰 설정, 정적 자원 설정 (`.jsp`, `/asset` 등) | `ViewResolver`, `@EnableWebMvc` |
| **MyBatisConfig** | DB 연결 및 MyBatis 설정 (SQL 실행용) | `.env`, `mybatis-config.xml` 읽음 |

---

---

### Spring MVC와 일반 서블릿 방식 비교: ✅ 목표: 게시판 기능 만들기  
> "/post/list"로 접속하면 글 목록이 나오고  
> "/post/write"로 글을 등록하면 저장된 글 목록에 반영됨

---

**1️⃣ Spring 없이 만드는 방식 (서블릿 방식)**

---

**📁 구조 (Spring 없음)**

```
webapp/
├── WEB-INF/views/
│   ├── list.jsp
│   └── write.jsp
├── model/
│   └── Post.java
├── repository/
│   └── PostRepository.java
├── servlet/
│   ├── PostListServlet.java
│   └── PostWriteServlet.java
```

---

**✅ Post.java**

```java
public class Post {
    private String title;
    private String content;
    // 생성자, getter, setter 생략
}
```

---

**✅ PostRepository.java (글 저장소, 메모리 버전)**

```java
public class PostRepository {
    private static final List<Post> posts = new ArrayList<>();

    public static void save(Post post) {
        posts.add(post);
    }

    public static List<Post> findAll() {
        return posts;
    }
}
```

---

**✅ PostListServlet.java**

```java
@WebServlet("/post/list")
public class PostListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Post> posts = PostRepository.findAll();
        req.setAttribute("posts", posts);
        req.getRequestDispatcher("/WEB-INF/views/list.jsp").forward(req, res);
    }
}
```

---

**✅ PostWriteServlet.java**

```java
@WebServlet("/post/write")
public class PostWriteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        PostRepository.save(new Post(title, content));
        res.sendRedirect("/post/list");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/write.jsp").forward(req, res);
    }
}
```

---

**✅ list.jsp**

```jsp
<h1>글 목록</h1>
<c:forEach var="post" items="${posts}">
    <div>
        <h2>${post.title}</h2>
        <p>${post.content}</p>
    </div>
</c:forEach>
<a href="/post/write">글쓰기</a>
```

---

**✅ write.jsp**

```jsp
<h1>글쓰기</h1>
<form action="/post/write" method="post">
    제목: <input name="title"><br>
    내용: <textarea name="content"></textarea><br>
    <button type="submit">저장</button>
</form>
```

---

**⚠️ 여기까지의 문제점 (Spring 없이)**

- 서블릿 2개 (`list`, `write`) 각각 직접 작성
- `doGet()`, `doPost()` 구분하고 `getParameter()`, `setAttribute()` 매번 반복
- Redirect, Forward, View 경로 다 직접 설정
- Bean 관리 없음 → 객체 상태 공유/주입 어려움
- 테스트, 확장, 에러처리 어려움

---

---

### 2️⃣ **같은 기능, Spring MVC로 만들면?**

---

**📁 구조 (Spring MVC)**

```
src/
├── controller/
│   └── PostController.java
├── model/
│   └── Post.java
├── repository/
│   └── PostRepository.java
├── templates/
│   ├── list.jsp
│   └── write.jsp
```

---

**✅ Post.java, PostRepository.java (똑같음)**

---

**✅ PostController.java**

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

---

**✅ list.jsp, write.jsp (거의 동일)**

---

**🧠 Spring MVC에서 달라진 점**

| 기능 | 서블릿 방식 | Spring MVC |
|------|-------------|------------|
| 요청 처리 | 서블릿 직접 작성 | `@Controller + @GetMapping` |
| 파라미터 받기 | `request.getParameter()` | `@RequestParam` 자동 처리 |
| 데이터 전달 | `request.setAttribute()` | `model.addAttribute()` |
| 화면 이동 | `dispatcher.forward()` | `return "viewName"` |
| URL 연결 | `@RequestMapping`, 자동 매핑 | 직접 URL 등록 필요 없음 |
| DI 지원 | 없음 | `@Autowired`, 생성자 주입 |
