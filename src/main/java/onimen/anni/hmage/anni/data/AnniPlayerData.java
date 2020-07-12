package onimen.anni.hmage.anni.data;

import java.util.EnumMap;
import java.util.Map;

import onimen.anni.hmage.anni.AnniKillType;

/**
 * 1キルごとに２回カウントされるため、0.5づつ加算する
 */
public class AnniPlayerData {

  private AnniTeamColor teamColor;

  private final String playerName;

  private Map<AnniTeamColor, Double> meleeKillCount = new EnumMap<>(AnniTeamColor.class);

  private Map<AnniTeamColor, Double> bowKillCount = new EnumMap<>(AnniTeamColor.class);

  /** 近接攻撃のキル数 */
  private double meleeCount;

  /** 弓のキル数 */
  private double bowCount;

  private double nexusDamageCount;

  /** 死亡数 */
  private double deathCount;

  private Map<AnniTeamColor, Double> nexusDamageMap = new EnumMap<>(AnniTeamColor.class);

  public AnniPlayerData(String playerName, AnniTeamColor teamColor) {
    this.playerName = playerName;
    this.teamColor = teamColor;
  }

  public int getMeleeCount() {
    return (int) meleeCount;
  }

  public int getBowCount() {
    return (int) bowCount;
  }

  /**
   * キル数をカウントする。
   *
   * @param killType キル種別
   */
  public void incrementCount(AnniKillType killType, AnniTeamColor teamColor) {
    if (killType == AnniKillType.MELEE) {
      meleeCount += 0.5;
      meleeKillCount.compute(teamColor, (k, v) -> v == null ? 0.5 : v.intValue() + 0.5);
    } else {
      bowCount += 0.5;
      bowKillCount.compute(teamColor, (k, v) -> v == null ? 0.5 : v.intValue() + 0.5);
    }
  }

  /**
   * ネクサスダメージをカウントする。
   *
   * @param teamColor ネクサスを削ったチーム
   */
  public void nexusDamage(AnniTeamColor teamColor) {
    nexusDamageMap.compute(teamColor, (k, v) -> v == null ? 0.5 : v.intValue() + 0.5);
    nexusDamageCount += 0.5;
  }

  /**
   * 総ネクサスダメージを取得する。
   *
   * @return 総ネクサスダメージ
   */
  public int getNexusDamageCount() {
    return (int) nexusDamageCount;
  }

  /**
   * 総キル数を取得する。
   *
   * @return 総キル数
   */
  public int getTotalKillCount() {
    return (int) (bowCount + meleeCount);
  }

  public AnniTeamColor getTeamColor() {
    return teamColor;
  }

  public void setTeamColor(AnniTeamColor teamColor) {
    this.teamColor = teamColor;
  }

  public String getPlayerName() {
    return playerName;
  }

  public int getDeathCount() {
    return (int) deathCount;
  }

  public void incrementDeathCount() {
    deathCount += 0.5;
  }
}
