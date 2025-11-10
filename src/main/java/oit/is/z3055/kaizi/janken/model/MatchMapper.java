package oit.is.z3055.kaizi.janken.model;

import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MatchMapper {

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand, isActive) "
      + "VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand}, #{isActive})")
  void insertMatch(Match match);

  @Select("SELECT * FROM matches WHERE isActive = TRUE")
  List<Match> selectActiveMatches();

  // ★ 追加：ログインユーザに関係する「アクティブな最新1件」を取得
  @Select("""
      SELECT * FROM matches
      WHERE isActive = TRUE AND (user1 = #{userId} OR user2 = #{userId})
      ORDER BY id DESC
      LIMIT 1
      """)
  Match selectActiveMatchByUserId(int userId);

  @Update("UPDATE matches SET isActive = FALSE WHERE id = #{id}")
  void deactivateMatch(int id);
}
