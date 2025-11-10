package oit.is.z3055.kaizi.janken.model;

import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MatchInfoMapper {

  @Select("SELECT * FROM matchinfo WHERE isActive = TRUE")
  List<MatchInfo> selectActiveMatchInfo();

  @Select("SELECT * FROM matchinfo WHERE isActive = TRUE AND (user1 = #{id} OR user2 = #{id})")
  List<MatchInfo> selectActiveMatchInfoByUser(int id);

  @Insert("INSERT INTO matchinfo (user1, user2, user1Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  void insertMatchInfo(MatchInfo matchInfo);

  @Update("UPDATE matchinfo SET isActive = FALSE WHERE id = #{id}")
  void deactivateMatchInfo(int id);
}
