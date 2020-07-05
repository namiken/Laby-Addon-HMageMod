package onimen.anni.hmage.listener;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import onimen.anni.hmage.anni.AnniObserver;
import onimen.anni.hmage.anni.AnniObserverManager;
import onimen.anni.hmage.anni.data.AnniPlayerData;
import onimen.anni.hmage.anni.data.GameInfo;
import onimen.anni.hmage.gui.AnniHistoryList;
import onimen.anni.hmage.util.GuiScreenUtils;

public class ForgeEventListener {

  public static KeyBinding openSettingsKey = new KeyBinding("Open Anni History", Keyboard.KEY_P,
      "key.categories.gameplay");
  public static KeyBinding showAnniRankingTab = new KeyBinding("Instant Anni Ranking", Keyboard.KEY_H,
      "key.categories.gameplay");

  @SubscribeEvent
  public void onPlayerTick(ClientTickEvent event) {
    if (openSettingsKey.isPressed()) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen == null) {
        mc.displayGuiScreen(new AnniHistoryList());
      }
    }

    AnniObserverManager anniObserverMap = AnniObserverManager.getInstance();
    anniObserverMap.onClientTick(event);
  }

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent event) {

    Minecraft mc = Minecraft.getMinecraft();

    if (mc.gameSettings.showDebugInfo) { return; }

    if (mc.currentScreen == null) {
      if (showAnniRankingTab.isKeyDown()) {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        renderAnniRanking(resolution.getScaledWidth(), resolution.getScaledHeight());
      }
    }
  }

  private void renderAnniRanking(int width, int height) {
    AnniObserver anniObserver = AnniObserverManager.getInstance().getAnniObserver();
    Minecraft mc = Minecraft.getMinecraft();

    if (anniObserver == null)
      return;

    GameInfo gameInfo = anniObserver.getGameInfo();

    List<AnniPlayerData> killRanking = gameInfo.getTotalKillRanking(10);
    List<AnniPlayerData> nexusRanking = gameInfo.getNexusRanking(10);

    GlStateManager.disableLighting();

    GuiScreenUtils.drawRankingLeft("Kills in this Game", killRanking, mc.fontRenderer, height / 10, width / 4,
        d -> String.format("%dK", d.getTotalKillCount()));

    GuiScreenUtils.drawRankingRight("Nexus damage in this Game", nexusRanking, mc.fontRenderer, height / 10,
        width / 4 * 3,
        d -> String.format("%dD", d.getNexusDamageCount()));

  }
}
