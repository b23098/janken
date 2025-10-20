package oit.is.z3055.kaizi.janken.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import oit.is.z3055.kaizi.janken.model.Entry;

@Controller
public class JankenController {

  private final Entry entry;

  public JankenController(Entry entry) {
    this.entry = entry;
  }

  // 初期画面
  @GetMapping("/janken")
  public String janken(Model model, Authentication auth) {
    String currentUser = (auth != null) ? auth.getName() : "anonymous";
    model.addAttribute("currentUser", currentUser);
    model.addAttribute("entries", entry.list());
    return "janken";
  }

  // 結果処理
  @GetMapping("/janken/result")
  public String jankenResult(@RequestParam String hand, Model model, Authentication auth) {
    String currentUser = (auth != null) ? auth.getName() : "anonymous";

    // 相手（CPU）の手
    String[] hands = { "グー", "チョキ", "パー" };
    String cpuHand = hands[(int) (Math.random() * 3)];

    // 勝敗判定
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

    // モデルに値を渡す
    model.addAttribute("currentUser", currentUser);
    model.addAttribute("entries", entry.list());
    model.addAttribute("userHand", hand);
    model.addAttribute("cpuHand", cpuHand);
    model.addAttribute("result", result);

    return "janken";
  }
}
