package oit.is.z3055.kaizi.janken.model;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class Entry {
  private final Set<String> users = ConcurrentHashMap.newKeySet();

  public void add(String name) {
    if (name != null) {
      users.add(name);
    }
  }

  public void remove(String name) {
    if (name != null) {
      users.remove(name);
    }
  }

  public List<String> list() {
    return users.stream().sorted().collect(Collectors.toList());
  }
}
