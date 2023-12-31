package app.dao.likes;

import app.dao.DaoFrame;
import app.dto.likes.request.LikesSelectForPage;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.List;

public interface LikesDaoFrame<K, V> extends DaoFrame<K, V> {
  List<Long> selectAllProduct(LikesSelectForPage likesSelectForPage, SqlSession session)
      throws SQLException;

  public Integer selectTotalPage(Long memberId, Integer perPage, SqlSession session)
      throws SQLException;
}
