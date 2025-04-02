package org.example.searchbookmark.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

```
MyBatis란?
자바 객체와 SQL을 연결해주는 프레임워크
즉, DB에 직접 SQL을 날리되,
자바 코드에서 SQL을 간편하게 실행하고, 결과를 객체로 바꿔주는 도구예요.

    [자바 코드] ⇄ [MyBatis Mapper] ⇄ [SQL] ⇄ [DB]

이 클래스는:
SqlSessionFactory라는 MyBatis 핵심 객체를 생성하고
환경변수(.env 파일)를 통해 DB 정보를 불러오고
mybatis-config.xml 설정 파일을 로드해서
MyBatis와 DB를 연결하는 설정을 초기화하는 역할을 합니다.
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
```

// MyBatis 설정을 자바 코드로 구성한 클래스
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
