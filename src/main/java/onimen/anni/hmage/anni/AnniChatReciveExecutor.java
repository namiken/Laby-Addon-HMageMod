package onimen.anni.hmage.anni;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.text.ChatType;
import onimen.anni.hmage.LabyHmageMod;
import onimen.anni.hmage.anni.data.AnniTeamColor;
import onimen.anni.hmage.util.JavaUtil;

public class AnniChatReciveExecutor {

  /** キルログのパターン */
  private static Pattern killChatPattern = Pattern
      .compile("§(.)(.+)§(.)\\((.+)\\) (shot|killed) §(.)(.+)§(.)\\((.+)\\).*");

  /** ネクサスダメージのパターン */
  private static Pattern nexusChatPattern = Pattern
      .compile("§(?<attackColor>.)(?<attacker>.+)§(.) has damaged the §(?<damageColor>.)(.+) team's nexus!.*");
  private static Pattern nexusChatPattern2 = Pattern
      .compile("§(?<damageColor>.)(.+) team's§(.) nexus is under attack by §(?<attackColor>.)(?<attacker>.+).*");

  private static List<Pattern> nexusChatPatterns = Arrays.asList(nexusChatPattern, nexusChatPattern2);

  /** 職業変更のパターン */
  private static Pattern changeJobPattern = Pattern.compile("\\[Class\\] (?<job>.+) Selected");

  private static BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>();

  private static ChatParseRunner target = null;

  /**
   * チャットを受け取ったときの処理。
   *
   * @param textComponent チャット
   */
  public static void onReceiveChat(String message) {
    if (JavaUtil.isEmpty(message)) { return; }

    blockingQueue.add(message);
  }

  public synchronized static void startThread() {
    if (target != null) { return; }

    target = new ChatParseRunner();
    Thread thread = new Thread(target);
    thread.setDaemon(true);
    thread.start();

    LabyHmageMod.getLogger().info("Start, Chat Receive Thread!!");
  }

  public static ChatParseRunner getTarget() {
    return target;
  }

  static public class ChatParseRunner implements Runnable {

    private long lastExecuteTime = -1;

    @Override
    public void run() {
      while (true) {
        try {
          lastExecuteTime = System.currentTimeMillis();

          String message = blockingQueue.poll(200, TimeUnit.SECONDS);
          if (message == null) {
            continue;
          }

          AnniObserver anniObserver = AnniObserverMap.getInstance().getAnniObserver();
          if (anniObserver == null) { return; }

          //キルログパターン
          Matcher matcher = killChatPattern.matcher(message);
          if (matcher.matches()) {
            //キル数を加算
            AnniTeamColor killerTeam = AnniTeamColor.findByColorCode(matcher.group(1));
            AnniTeamColor deadTeam = AnniTeamColor.findByColorCode(matcher.group(6));
            AnniKillType killType = matcher.group(5).equalsIgnoreCase("shot") ? AnniKillType.SHOT : AnniKillType.MELEE;
            anniObserver.getGameInfo().addKillCount(matcher.group(2), killerTeam, matcher.group(7), deadTeam, killType);
            return;
          }

          //ネクダメログのパターン
          Matcher matcher2 = null;
          //パターンに一致するかどうかを調べる
          for (Pattern pattern : nexusChatPatterns) {
            Matcher temp = pattern.matcher(message);
            if (temp.matches()) {
              matcher2 = temp;
              break;
            }
          }
          if (matcher2 != null) {
            //ネクダメを加算
            AnniTeamColor attackerColor = AnniTeamColor.findByColorCode(matcher2.group("attackColor"));
            AnniTeamColor damageColor = AnniTeamColor.findByColorCode(matcher2.group("damageColor"));
            anniObserver.getGameInfo().addNexusDamageCount(matcher2.group("attacker"),
                attackerColor, damageColor);
            return;
          }

          //職業変更のパターン
          Matcher matcher3 = changeJobPattern.matcher(message);
          if (matcher3.matches()) {
            //変更後の職業を設定
            ClassType classType = ClassType.getClassTypeFromName(matcher3.group("job"));
            if (classType != null) {
              anniObserver.getGameInfo().setClassType(classType);
            }
            return;
          }

        } catch (Throwable e) {
          LabyHmageMod.getLogger().error(e, "AnniChatReciveExecutor Error");
        }
      }
    }

    public long getLastExecuteTime() {
      return lastExecuteTime;
    }
  }

  static class ChatReciveTask {

    private final String message;

    private final ChatType chatType;

    public ChatReciveTask(String message, ChatType chatType) {
      this.message = message;
      this.chatType = chatType;
    }

    public String getMessage() {
      return message;
    }

    public ChatType getChatType() {
      return chatType;
    }

  }
}
