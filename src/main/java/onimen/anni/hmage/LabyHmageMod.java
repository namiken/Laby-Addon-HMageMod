package onimen.anni.hmage;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageReceiveEvent;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import onimen.anni.hmage.anni.AnniChatReciveExecutor;
import onimen.anni.hmage.listener.ForgeEventListener;
import onimen.anni.hmage.module.AcroJumpModule;
import onimen.anni.hmage.module.AnniStatusModule;
import onimen.anni.hmage.util.HMageLogger;
import onimen.anni.hmage.util.ShotbowUtils;

@Mod(modid = LabyHmageMod.MODID, name = LabyHmageMod.NAME, version = LabyHmageMod.VERSION)
public class LabyHmageMod extends LabyModAddon {
  public static final String MODID = "hmage";
  public static final String NAME = "HMage";
  public static final String VERSION = "1.0.1";

  private static HMageLogger logger = new HMageLogger();

  public static HMageLogger getLogger() {
    return logger;
  }

  /**
   * Called when the addon gets enabled
   */
  @Override
  public void onEnable() {
    AnniChatReciveExecutor.startThread();

    //チャットログのEvent
    this.api.getEventManager().register(new MessageReceiveEvent() {
      @Override
      public boolean onReceive(String arg0, String arg1) {
        if (ShotbowUtils.isShotbow(Minecraft.getMinecraft())) {
          AnniChatReciveExecutor.onReceiveChat(arg1);
        }
        return false;
      }
    });

    //キーの設定を行う
    Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils
        .add(Minecraft.getMinecraft().gameSettings.keyBindings, ForgeEventListener.openSettingsKey);

    //Forge Event Listenerの登録
    getApi().registerForgeListener(new ForgeEventListener());

    //モジュールの追加
    getApi().registerModule(new AcroJumpModule());
    getApi().registerModule(new AnniStatusModule());
  }

  /**
   * Called when this addon's config was loaded and is ready to use
   */
  @Override
  public void loadConfig() {

  }

  /**
   * Called when the addon's ingame settings should be filled
   *
   * @param subSettings a list containing the addon's settings' elements
   */
  @Override
  protected void fillSettings(List<SettingsElement> subSettings) {

  }
}
