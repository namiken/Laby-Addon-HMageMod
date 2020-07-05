package onimen.anni.hmage.anni;

import java.util.Map;
import java.util.UUID;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.BossInfo.Color;
import onimen.anni.hmage.anni.data.AnniTeamColor;

public class AnniLogic {

  private static final String MAP_PREFIX = ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Map: ";

  /**
   * Anniをプレイ中かどうかを判別する。
   *
   * @param mc Minecraft
   * @return プレイ中の場合はtrue
   */
  public boolean isInAnniGame(Minecraft mc) {
    Scoreboard scoreboard = mc.world.getScoreboard();
    if (scoreboard == null) { return false; }

    ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(1);
    if (scoreobjective == null) { return false; }
    String displayName = scoreobjective.getDisplayName();

    //Map名がある場合は試合中と判断
    return displayName.contains(MAP_PREFIX);
  }

  /**
   * 自身のチームを取得する。
   *
   * @param mc Minecraft
   * @return 自身のチーム
   */
  public AnniTeamColor getTeamColor(Minecraft mc) {
    Scoreboard scoreboard = mc.world.getScoreboard();
    ScorePlayerTeam team = scoreboard.getPlayersTeam(mc.player.getName());
    if (team == null) { return AnniTeamColor.NO_JOIN; }
    return AnniTeamColor.findByTeamName(team.getDisplayName().replaceFirst("§.", ""));
  }

  /**
   * 現在プレイ中のMapを取得する。
   *
   * @param mc Minecraft
   * @return 現在ゲーム中のMap
   */
  public String getMap(Minecraft mc) {
    Scoreboard scoreboard = mc.world.getScoreboard();
    ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(1);

    if (scoreobjective == null) { return null; }
    String displayName = scoreobjective.getDisplayName();

    if (!displayName.contains(MAP_PREFIX)) { return null; }

    return displayName.replace(MAP_PREFIX, "");
  }

  public PhaseData getPhaseData(Map<UUID, BossInfoClient> bossMap) {
    for (BossInfoClient bossInfo : bossMap.values()) {
      //フェーズを表示するボスバーは青色なので
      if (bossInfo.getColor() != Color.BLUE) {
        continue;
      }
      String name = bossInfo.getName().getUnformattedText();
      GamePhase nextPhase = GamePhase.getGamePhasebyText(name);
      int phaseTime = getPhaseTime(name, nextPhase);

      return new PhaseData(nextPhase, phaseTime);
    }

    return null;
  }

  private int getPhaseTime(String name, GamePhase nextPhase) {
    if (!nextPhase.hasCountdonw()) { return -1; }

    String timeString = name.substring(name.indexOf(" - ") + 3);
    String[] split = timeString.split(":");

    return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
  }

  class PhaseData {
    private final GamePhase phase;

    private final int time;

    public PhaseData(GamePhase phase, int time) {
      this.phase = phase;
      this.time = time;
    }

    public GamePhase getPhase() {
      return phase;
    }

    public int getTime() {
      return time;
    }

  }
}
