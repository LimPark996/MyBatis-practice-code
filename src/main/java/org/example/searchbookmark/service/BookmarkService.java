package org.example.searchbookmark.service;

import org.apache.ibatis.session.SqlSession;
import org.example.searchbookmark.config.MyBatisConfig;
import org.example.searchbookmark.model.mapper.BookmarkMapper;
import org.example.searchbookmark.model.vo.KeywordSearch;
import org.example.searchbookmark.util.MyLogger;
import org.springframework.stereotype.Service;

@Service
public class BookmarkService {
    final public MyLogger logger = new MyLogger(this.getClass().getName());

    public String createBookmark(KeywordSearch keywordSearch) {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            BookmarkMapper mapper = session.getMapper(BookmarkMapper.class);
            int count = mapper.insertBookmark(keywordSearch);
            logger.info(count + " bookmark inserted");
            String uuid = mapper.checkLastID();
            session.commit(); //insert, update, delete는 commit 필수예요 (데이터를 바꾸니까)
            // 우리가 직접 commit()을 호출해줘야 → 그제서야 DB에 **"진짜 저장"**이 됩니다!
            return uuid;
        }
    }

    public KeywordSearch readOneBookmark(String uuid) {
        try (SqlSession session = MyBatisConfig.getSqlSessionFactory().openSession()) {
            BookmarkMapper mapper = session.getMapper(BookmarkMapper.class);
            return mapper.getOneBookmark(uuid);
        }
    }
}
