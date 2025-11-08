package oit.is.z3055.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {

  @Select("SELECT * FROM matches")
  ArrayList<Match> selectAllMatches();

  @Select("SELECT * FROM matches WHERE isActive = FALSE")
  ArrayList<Match> selectFinishedMatches();

  @Select("SELECT * FROM matches WHERE isActive = TRUE")
  ArrayList<Match> selectActiveMatches();

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand, isActive) "
      + "VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand}, #{isActive})")
  void insertMatch(Match match);
}
