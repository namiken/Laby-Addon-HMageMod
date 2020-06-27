package onimen.anni.hmage.listener;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import onimen.anni.hmage.anni.AnniObserverMap;
import onimen.anni.hmage.gui.AnniHistoryList;

public class ForgeEventListener {

  public static KeyBinding openSettingsKey = new KeyBinding("hmage.key.settings", Keyboard.KEY_P,
      "key.categories.hmage");

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

    AnniObserverMap anniObserverMap = AnniObserverMap.getInstance();
    if (anniObserverMap.getAnniObserver() != null) {
      anniObserverMap.getAnniObserver().onClientTick(event);
    }
  }
}
