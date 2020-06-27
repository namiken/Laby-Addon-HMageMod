package onimen.anni.hmage.anni;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import net.labymod.addon.AddonLoader;
import net.minecraft.client.Minecraft;
import onimen.anni.hmage.LabyHmageMod;
import onimen.anni.hmage.anni.data.GameInfo;

public class AnniObserverMap {

  private static AnniObserverMap instance = null;

  public static AnniObserverMap getInstance() {
    if (instance == null) {
      historyDataDir = new File(AddonLoader.getConfigDirectory(), "anni");
      historyDataDir.mkdir();
      instance = new AnniObserverMap();
    }
    return instance;
  }

  @Nullable
  private String playingServerName;
  private final Map<String, AnniObserver> anniObserverMap;
  private static File historyDataDir;

  private AnniObserverMap() {
    anniObserverMap = Maps.newHashMap();
  }

  public void setAnniObserver(String serverName, GamePhase phase) {
    setAnniObserver(serverName, phase, false);
  }

  public void setAnniObserver(String serverName, GamePhase phase, boolean force) {
    playingServerName = serverName;

    boolean canPutNewObserver = false;

    if (anniObserverMap.containsKey(serverName)) {
      AnniObserver anniObserver = anniObserverMap.get(serverName);
      if (anniObserver != null) {
        if (anniObserver.getGameInfo().getGamePhase().getValue() > phase.getValue()) {
          LabyHmageMod.getLogger().info("detect change game, before phase:" + anniObserver.getGameInfo().getGamePhase()
              + ", next phase:" + phase);
          canPutNewObserver = true;
        } else {
          LabyHmageMod.getLogger().info("detect change game, before phase:" + anniObserver.getGameInfo().getGamePhase()
              + ", next phase:" + phase);
        }
      }
    } else {
      canPutNewObserver = true;
    }

    if (force || canPutNewObserver) {
      LabyHmageMod.getLogger().info("New Annihilation Observer created");
      anniObserverMap.put(serverName, new AnniObserver(Minecraft.getMinecraft()));
    }

    getAnniObserver().onJoinGame();
    LabyHmageMod.getLogger().info("Playing server name: " + playingServerName);
  }

  public void unsetAnniObserver() {
    LabyHmageMod.getLogger().info("Stop observing anni: " + playingServerName);
    getAnniObserver().onLeaveGame();
    this.playingServerName = null;
  }

  @Nullable
  public AnniObserver getAnniObserver() {
    if (this.playingServerName == null)
      return null;
    return anniObserverMap.get(playingServerName);
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

  public String getPlayingServerName() {
    return playingServerName;
  }
}
