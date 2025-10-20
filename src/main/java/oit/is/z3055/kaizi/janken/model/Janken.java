package oit.is.z3055.kaizi.janken.model;

public class Janken {
  private final String myHand;
  private final String cpuHand = "Gu"; // 固定
  private final String result;

  public Janken(String myHand) {
    this.myHand = myHand;
    this.result = judge();
  }

  private String judge() {
    if (myHand.equals(cpuHand))
      return "Draw!";
    if (myHand.equals("Pa") && cpuHand.equals("Gu"))
      return "You Win!";
    if (myHand.equals("Gu") && cpuHand.equals("Choki"))
      return "You Win!";
    if (myHand.equals("Choki") && cpuHand.equals("Pa"))
      return "You Win!";
    return "You Lose!";
  }

  public String getMyHand() {
    return myHand;
  }

  public String getCpuHand() {
    return cpuHand;
  }

  public String getResult() {
    return result;
  }
}
