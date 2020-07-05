package onimen.anni.hmage.anni.data;

import onimen.anni.hmage.anni.GamePhase;

public class MatchIdentify {
  private String map;

  private GamePhase phase;

  private int remindTime;

  private long recordTime;

  public void setMap(String map) {
    this.map = map;
  }

  public void setPhase(GamePhase phase, int remindTime) {
    this.phase = phase;
    this.remindTime = remindTime;
    this.recordTime = System.currentTimeMillis();
  }

  public String getMap() {
    return map;
  }

  public GamePhase getPhase() {
    return phase;
  }

  public int getRemindTime() {
    return remindTime;
  }

  public long getRecordTime() {
    return recordTime;
  }

}
