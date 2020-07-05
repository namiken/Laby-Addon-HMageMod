package onimen.anni.hmage.anni;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.gson.Gson;

import net.labymod.addon.AddonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import onimen.anni.hmage.LabyHmageMod;
import onimen.anni.hmage.anni.AnniLogic.PhaseData;
import onimen.anni.hmage.anni.data.AnniTeamColor;
import onimen.anni.hmage.anni.data.GameInfo;
import onimen.anni.hmage.util.JavaUtil;

public class AnniObserverManager {

  private static AnniObserverManager instance = null;

  public static AnniObserverManager getInstance() {
    if (instance == null) {
      historyDataDir = new File(AddonLoader.getConfigDirectory(), "anni");
      historyDataDir.mkdir();
      instance = new AnniObserverManager();
    }
    return instance;
  }

  @Nullable
  private final List<AnniObserver> anniObserverMap = new ArrayList<>();

  private AnniObserver nowObserver = null;

  private static File historyDataDir;

  private Map<UUID, BossInfoClient> bossInfoMap = null;

  private int tickLeftWhileNoAnniScoreboard = 0;

  private AnniLogic anniLogic = new AnniLogic();

  private AnniObserverManager() {
  }

  public void onClientTick(ClientTickEvent event) {
    if (event.phase != Phase.END) { return; }

    Minecraft mc = Minecraft.getMinecraft();
    //ゲーム中でないならリセット
    if (mc.ingameGUI == null || mc.world == null) {
      unsetAnniObserver();
      return;
    }

    //Anniをプレイ中かどうか確認
    if (!anniLogic.isInAnniGame(mc)) {
      tickLeftWhileNoAnniScoreboard++;
      if (tickLeftWhileNoAnniScoreboard > 100) {
        unsetAnniObserver();
      }
      return;
    }

    //スコアボードが存在しないカウントを初期化
    tickLeftWhileNoAnniScoreboard = 0;

    //ボスゲージ取得
    if (this.bossInfoMap == null) {
      this.bossInfoMap = getBossInfoMap(mc.ingameGUI.getBossOverlay());
    }
    //ボスゲージが取得できない場合は何もしない
    if (this.bossInfoMap == null) { return; }

    //observerがなければ作成する
    if (nowObserver == null) {
      reloadAnniObserver(mc);
    }
    if (nowObserver == null) { return; }

    GameInfo gameInfo = nowObserver.getGameInfo();
    //自身のチームを更新
    AnniTeamColor meTeamColor = gameInfo.getMeTeamColor();
    if (meTeamColor == null || meTeamColor == AnniTeamColor.NO_JOIN) {
      AnniTeamColor newTeamColor = anniLogic.getTeamColor(mc);
      gameInfo.getMePlayerData().setTeamColor(newTeamColor);
    }

    //フェーズを更新
    PhaseData phaseData = anniLogic.getPhaseData(bossInfoMap);
    if (phaseData != null) {
      gameInfo.setGamePhase(phaseData.getPhase());
    }
  }

  protected void reloadAnniObserver(Minecraft mc) {
    //チームの色
    AnniTeamColor teamColor = anniLogic.getTeamColor(mc);

    //map
    String map = anniLogic.getMap(mc);
    if (JavaUtil.isEmpty(map)) { return; }

    //phase
    PhaseData phaseData = anniLogic.getPhaseData(bossInfoMap);
    if (phaseData == null) { return; }

    AnniObserver nowEntry = null;
    for (AnniObserver entry : anniObserverMap) {
      GameInfo gameInfo = entry.getGameInfo();
      //mapの確認
      if (!map.equals(gameInfo.getMapName())) {
        continue;
      }

      //チームの確認
      if (gameInfo.getMeTeamColor() != teamColor) {
        continue;
      }

      //フェーズの確認
      if (gameInfo.getGamePhase().ordinal() > phaseData.getPhase().ordinal()) {
        continue;
      }

      nowEntry = entry;
    }

    //TODO もっと工夫したほうが良いかも
    if (nowEntry != null) {
      this.nowObserver = nowEntry;
    } else {
      this.nowObserver = new AnniObserver(mc, map);
      anniObserverMap.add(this.nowObserver);
      LabyHmageMod.getLogger().info("Crate New Observer. map=" + map);
    }

    this.nowObserver.onJoinGame();
    LabyHmageMod.getLogger().info("Joining Game. map=" + map);
  }

  @SuppressWarnings("unchecked")
  private Map<UUID, BossInfoClient> getBossInfoMap(GuiBossOverlay bossOverlay) {
    if (bossOverlay != null) {

      Field[] declaredFields = bossOverlay.getClass().getDeclaredFields();

      for (Field field : declaredFields) {

        int modifiers = field.getModifiers();

        if (!Modifier.isPrivate(modifiers) || !Modifier.isFinal(modifiers))
          continue;

        if (!Map.class.isAssignableFrom(field.getType()))
          continue;

        try {
          field.setAccessible(true);
          return Collections.synchronizedMap((Map<UUID, BossInfoClient>) field.get(bossOverlay));
        } catch (IllegalArgumentException | IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public void unsetAnniObserver() {
    AnniObserver anniObserver = getAnniObserver();
    if (anniObserver != null) {
      LabyHmageMod.getLogger().info("Stop observing anni");
      anniObserver.onLeaveGame();
    }
    this.bossInfoMap = null;
    this.nowObserver = null;
  }

  @Nullable
  public AnniObserver getAnniObserver() {

    return nowObserver;
  }

  public List<GameInfo> getGameInfoList() {
    try {
      Set<GameInfo> gameInfoMap = new TreeSet<>((f1, f2) -> Long.compare(f1.getGameTimestamp(), f2.getGameTimestamp()));

      Gson gson = new Gson();

      //ファイルから試合情報を読み込み
      File[] listFiles = historyDataDir.listFiles();
      if (listFiles == null) {
        listFiles = new File[0];
      }
      List<File> collect = Arrays.stream(listFiles).sorted((f1, f2) -> f1.getName().compareTo(f2.getName()))
          .collect(Collectors.toList());
      for (File file : collect) {
        FileReader fileReader = new FileReader(file);
        GameInfo fromJson = gson.fromJson(fileReader, GameInfo.class);
        gameInfoMap.add(fromJson);
      }

      //監視中の試合を読み込み
      AnniObserver anniObserver = getAnniObserver();
      if (anniObserver != null) {
        gameInfoMap.remove(anniObserver.getGameInfo());
        gameInfoMap.add(anniObserver.getGameInfo());
      }
      return new ArrayList<>(gameInfoMap);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static File getHistoryDataDir() {
    return historyDataDir;
  }

}
