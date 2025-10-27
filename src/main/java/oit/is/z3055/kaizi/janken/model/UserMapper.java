package oit.is.z3055.kaizi.janken.model;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

  @Select("SELECT * FROM users")
  ArrayList<User> selectAllUsers();

  @Select("SELECT * FROM users WHERE id = #{id}")
  User selectById(int id);

  @Select("SELECT * FROM users WHERE name = #{name}")
  User selectByName(String name); // ←追加：ログインユーザの名前からIDを取得
}
