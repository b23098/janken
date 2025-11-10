package oit.is.z3055.kaizi.janken.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import oit.is.z3055.kaizi.janken.model.*;
import oit.is.z3055.kaizi.janken.service.AsyncKekka;

@Controller
public class JankenController {

  private final UserMapper userMapper;
  private final MatchInfoMapper matchInfoMapper;
  private final MatchMapper matchMapper;
  private final AsyncKekka asyncKekka;

  public JankenController(UserMapper userMapper, MatchInfoMapper matchInfoMapper,
      MatchMapper matchMapper, AsyncKekka asyncKekka) {
    this.userMapper = userMapper;
    this.matchInfoMapper = matchInfoMapper;
    this.matchMapper = matchMapper;
    this.asyncKekka = asyncKekka;
  }

  @GetMapping("/janken")
  public String showJanken(Model model, Authentication auth) {
    String loginUserName = auth.getName();
    model.addAttribute("loginUser", loginUserName);
    model.addAttribute("users", userMapper.selectAllUsers());
    List<MatchInfo> activeMatches = matchInfoMapper.selectActiveMatchInfo();
    model.addAttribute("activeMatches", activeMatches);
    return "janken";
  }

  @GetMapping("/match")
  public String showMatch(@RequestParam int id, Model model, Authentication auth) {
    String loginUserName = auth.getName();
    User loginUser = userMapper.selectByName(loginUserName);
    User opponent = userMapper.selectById(id);
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);
    return "match";
  }

  @GetMapping("/fight")
  public String fight(@RequestParam int id, @RequestParam String hand, Authentication auth, Model model) {
    String loginUserName = auth.getName();
    User loginUser = userMapper.selectByName(loginUserName);
    User opponent = userMapper.selectById(id);
    List<MatchInfo> existing = matchInfoMapper.selectActiveMatchInfoByUser(opponent.getId());
    if (existing.isEmpty()) {
      MatchInfo info = new MatchInfo();
      info.setUser1(loginUser.getId());
      info.setUser2(opponent.getId());
      info.setUser1Hand(hand);
      info.setIsActive(true);
      matchInfoMapper.insertMatchInfo(info);
    } else {
      MatchInfo info = existing.get(0);
      Match match = new Match();
      match.setUser1(info.getUser1());
      match.setUser2(info.getUser2());
      match.setUser1Hand(info.getUser1Hand());
      match.setUser2Hand(hand);
      match.setIsActive(true);
      matchMapper.insertMatch(match);
      matchInfoMapper.deactivateMatchInfo(info.getId());
    }
    model.addAttribute("loginUser", loginUserName);
    asyncKekka.asyncWaitForResult();
    return "wait";
  }

  @GetMapping("/api/result")
  @ResponseBody
  public Map<String, Object> apiResult(Authentication auth) {
    Map<String, Object> res = new HashMap<>();
    String loginUserName = auth.getName();
    User me = userMapper.selectByName(loginUserName);
    Match m = matchMapper.selectActiveMatchByUserId(me.getId());
    if (m == null) {
      res.put("active", true);
      return res;
    }
    String u1Name = userMapper.selectById(m.getUser1()).getName();
    String u2Name = userMapper.selectById(m.getUser2()).getName();
    String h1 = normalizeHand(m.getUser1Hand());
    String h2 = normalizeHand(m.getUser2Hand());
    String judge = judge(h1, h2, loginUserName, u1Name, u2Name);
    String resultText = String.format("%s(%s) vs %s(%s) → %s", u1Name, h1, u2Name, h2, judge);
    res.put("active", false);
    res.put("result", resultText);
    matchMapper.deactivateMatch(m.getId());
    return res;
  }

  private String normalizeHand(String s) {
    if (s == null)
      return "";
    return switch (s) {
      case "グー", "Gu", "gu" -> "グー";
      case "チョキ", "Choki", "choki" -> "チョキ";
      case "パー", "Pa", "pa" -> "パー";
      default -> s;
    };
  }

  private String judge(String h1, String h2, String loginUserName, String u1Name, String u2Name) {
    if (h1.equals(h2)) {
      return "あいこ";
    }
    boolean user1Wins = (h1.equals("グー") && h2.equals("チョキ"))
        || (h1.equals("チョキ") && h2.equals("パー"))
        || (h1.equals("パー") && h2.equals("グー"));
    if (user1Wins) {
      if (loginUserName.equals(u1Name)) {
        return "あなたの勝ち";
      } else {
        return "あなたの負け";
      }
    } else {
      if (loginUserName.equals(u2Name)) {
        return "あなたの勝ち";
      } else {
        return "あなたの負け";
      }
    }
  }
}
