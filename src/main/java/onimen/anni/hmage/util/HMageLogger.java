package onimen.anni.hmage.util;

public class HMageLogger {

  public void info(String msg) {
    System.out.println("[HMage Mod][Info] " + msg);
  }

  public void warn(String msg) {
    System.out.println("[HMage Mod][Warn] " + msg);
  }

  public void error(String msg) {
    System.out.println("[HMage Mod][Error] " + msg);
  }

  public void error(Throwable e, String msg) {
    System.out.println("[HMage Mod][Error] " + msg);
    e.printStackTrace();
  }
}
