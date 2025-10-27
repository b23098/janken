package oit.is.z3055.kaizi.janken.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3055.kaizi.janken.model.User;
import oit.is.z3055.kaizi.janken.model.UserMapper;
import oit.is.z3055.kaizi.janken.model.Match;
import oit.is.z3055.kaizi.janken.model.MatchMapper;

@Controller
public class JankenController {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private MatchMapper matchMapper;

  // /janken : ユーザ一覧と試合結果一覧を表示
  @GetMapping("/janken")
  public String janken(Authentication auth, Model model) {
    String currentUser = (auth != null) ? auth.getName() : "anonymous";
    ArrayList<User> users = userMapper.selectAllUsers();
    ArrayList<Match> matches = matchMapper.selectAllMatches();
    model.addAttribute("currentUser", currentUser);
    model.addAttribute("users", users);
    model.addAttribute("matches", matches);
    return "janken";
  }

  // /match?id=◯ と /janken/match?id=◯ の両方で受ける
  @GetMapping({ "/match", "/janken/match" })
  public String showMatch(@RequestParam int id, Authentication auth, Model model) {
    if (auth == null)
      return "redirect:/login";
    User loginUser = userMapper.selectByName(auth.getName());
    User opponent = userMapper.selectById(id);
    if (loginUser == null || opponent == null)
      return "redirect:/janken";
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);
    return "match";
  }

  // /fight?hand=◯&id=◯ と /janken/fight?... の両方で受ける
  @GetMapping({ "/fight", "/janken/fight" })
  public String fight(@RequestParam String hand, @RequestParam int id, Authentication auth, Model model) {
    if (auth == null)
      return "redirect:/login";
    User loginUser = userMapper.selectByName(auth.getName());
    User opponent = userMapper.selectById(id);
    if (loginUser == null || opponent == null)
      return "redirect:/janken";

    String cpuHand = "チョキ"; // 固定
    String result;
    if (hand.equals(cpuHand)) {
      result = "あいこです！";
    } else if ((hand.equals("グー") && cpuHand.equals("チョキ"))
        || (hand.equals("チョキ") && cpuHand.equals("パー"))
        || (hand.equals("パー") && cpuHand.equals("グー"))) {
      result = "あなたの勝ち！";
    } else {
      result = "あなたの負け...";
    }

    Match m = new Match();
    m.setUser1(loginUser.getId());
    m.setUser2(opponent.getId());
    m.setUser1Hand(hand);
    m.setUser2Hand(cpuHand);
    matchMapper.insertMatch(m);

    model.addAttribute("loginUser", loginUser);
    model.addAttribute("opponent", opponent);
    model.addAttribute("userHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);
    return "match";
  }
}
