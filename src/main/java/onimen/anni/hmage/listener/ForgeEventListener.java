package onimen.anni.hmage.listener;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import onimen.anni.hmage.anni.AnniObserverManager;
import onimen.anni.hmage.gui.AnniHistoryList;

public class ForgeEventListener {

  public static KeyBinding openSettingsKey = new KeyBinding("Open Anni History", Keyboard.KEY_P,
      "key.categories.gameplay");

  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {

    if (openSettingsKey.isPressed()) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen == null) {
        mc.displayGuiScreen(new AnniHistoryList());
      }
    }
  }

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
}
