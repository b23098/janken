package oit.is.z3055.kaizi.janken.model;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {

  @Select("SELECT * FROM matches")
  ArrayList<Match> selectAllMatches();
}
