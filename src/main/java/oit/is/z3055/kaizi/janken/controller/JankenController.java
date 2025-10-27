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
  UserMapper userMapper;

  @Autowired
  MatchMapper matchMapper;

  @GetMapping("/janken")
  public String janken(Model model, Authentication auth) {
    String currentUser = (auth != null) ? auth.getName() : "anonymous";

    ArrayList<User> users = userMapper.selectAllUsers();
    ArrayList<Match> matches = matchMapper.selectAllMatches();

    model.addAttribute("currentUser", currentUser);
    model.addAttribute("users", users);
    model.addAttribute("matches", matches);

    return "janken";
  }

  @GetMapping("/janken/result")
  public String jankenResult(@RequestParam String hand, Model model, Authentication auth) {
    String currentUser = (auth != null) ? auth.getName() : "anonymous";

    String[] hands = { "グー", "チョキ", "パー" };
    String cpuHand = hands[(int) (Math.random() * 3)];

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

    ArrayList<User> users = userMapper.selectAllUsers();
    ArrayList<Match> matches = matchMapper.selectAllMatches();

    model.addAttribute("currentUser", currentUser);
    model.addAttribute("users", users);
    model.addAttribute("matches", matches);
    model.addAttribute("userHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);

    return "janken";
  }
}
