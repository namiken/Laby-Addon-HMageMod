package onimen.anni.hmage.anni;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.annotation.Nonnull;

import com.google.common.io.Files;
import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import onimen.anni.hmage.anni.data.AnniTeamColor;
import onimen.anni.hmage.anni.data.GameInfo;

public class AnniObserver {

  @Nonnull
  private final GameInfo gameInfo;

  public AnniObserver(Minecraft mcIn, String map) {
    this.gameInfo = new GameInfo();
    this.gameInfo.setMapName(map);
  }

  public GameInfo getGameInfo() {
    return this.gameInfo;
  }

  public void onJoinGame() {
  }

  public void onLeaveGame() {

    if (gameInfo.getGamePhase().getValue() > 0 && gameInfo.getMeTeamColor() != AnniTeamColor.NO_JOIN) {
      //gameInfoを保存
      File historyDataDir = AnniObserverManager.getHistoryDataDir();
      Gson gson = new Gson();
      String json = gson.toJson(gameInfo);
      try {
        Files.write(json, new File(historyDataDir, gameInfo.getGameTimestamp() + ".txt"), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
        //握りつぶす
      }

      //昔の情報を削除
      File[] listFiles = historyDataDir.listFiles(f -> f.getName().endsWith(".txt"));
      Arrays.stream(listFiles).sorted((f1, f2) -> f2.compareTo(f1)).skip(20).forEach(f -> f.delete());

    }
  }
}
