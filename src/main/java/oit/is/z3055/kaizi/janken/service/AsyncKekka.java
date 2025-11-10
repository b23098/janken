package oit.is.z3055.kaizi.janken.service;

import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import oit.is.z3055.kaizi.janken.model.Match;
import oit.is.z3055.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {

  private final MatchMapper matchMapper;

  public AsyncKekka(MatchMapper matchMapper) {
    this.matchMapper = matchMapper;
  }

  @Async
  public void asyncWaitForResult() {
    boolean dbUpdated = false;
    while (!dbUpdated) {
      try {
        Thread.sleep(2000);
        List<Match> activeMatches = matchMapper.selectActiveMatches();
        if (!activeMatches.isEmpty()) {
          dbUpdated = true;
          for (Match m : activeMatches) {
            matchMapper.deactivateMatch(m.getId());
          }
        }
      } catch (InterruptedException e) {
        break;
      }
    }
  }
}
