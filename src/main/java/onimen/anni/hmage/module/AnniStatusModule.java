package onimen.anni.hmage.module;

import net.labymod.ingamegui.moduletypes.SimpleTextModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import onimen.anni.hmage.anni.AnniObserver;
import onimen.anni.hmage.anni.AnniObserverManager;
import onimen.anni.hmage.anni.data.GameInfo;
import onimen.anni.hmage.util.ShotbowUtils;

public class AnniStatusModule extends SimpleTextModule {

  @Override
  public String[] getValues() {
    AnniObserver anniObserver = AnniObserverManager.getInstance().getAnniObserver();
    if (anniObserver == null) { return new String[] { " -", " -" }; }
    GameInfo gameInfo = anniObserver.getGameInfo();
    return new String[] { " " + gameInfo.getKillCount(), " " + gameInfo.getNexusAttackCount() };
  }

  @Override
  public boolean isShown() {
    return ShotbowUtils.isShotbow(Minecraft.getMinecraft());
  }

  @Override
  public String[] getDefaultValues() {
    return new String[] { " 5", " 105" };
  }

  @Override
  public String[] getDefaultKeys() {
    return new String[] { "kills", "Nexus" };
  }

  @Override
  public String[] getKeys() {
    return new String[] { "kills", "Nexus" };
  }

  @Override
  public String getDescription() {
    return "Show Your Anni Status.";
  }

  @Override
  public IconData getIconData() {
    return new ControlElement.IconData(Material.DIAMOND_SWORD);
  }

  @Override
  public String getControlName() {
    return "Anni Kill Nexus Counter";
  }

  @Override
  public String getSettingName() {
    return "hmage.anni_status";
  }

  @Override
  public int getSortingId() {
    return 1;
  }

  @Override
  public void loadSettings() {

  }

}
