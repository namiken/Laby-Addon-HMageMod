package onimen.anni.hmage.module;

import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import onimen.anni.hmage.anni.AnniObserver;
import onimen.anni.hmage.anni.AnniObserverManager;
import onimen.anni.hmage.anni.ClassType;

public class AcroJumpModule extends SimpleModule {

  private long allowFlightChanged = 0;

  private boolean allowFlying = false;

  @Override
  public String getDescription() {
    return "Show Cooltime of Acrobat Jump.";
  }

  @Override
  public IconData getIconData() {
    return new ControlElement.IconData(Material.FEATHER);
  }

  @Override
  public String getSettingName() {
    return "hmage.acrojump";
  }

  @Override
  public int getSortingId() {
    return 0;
  }

  @Override
  public void loadSettings() {

  }

  @Override
  public String getControlName() {
    return "AcroJumpCT";
  }

  @Override
  public String getDefaultValue() {
    return " 8.25sec";
  }

  @Override
  public String getDisplayName() {
    return "AcroJump";
  }

  @Override
  public String getDisplayValue() {
    return getCooldownText();
  }

  @Override
  public boolean isShown() {
    return isAcrobat();
  }

  private boolean isAcrobat() {
    AnniObserverManager instance = AnniObserverManager.getInstance();

    AnniObserver anniObserver = instance.getAnniObserver();
    if (anniObserver == null) { return false; }

    ClassType classType = anniObserver.getGameInfo().getClassType();
    return classType == ClassType.ACROBAT;

  }

  private String getCooldownText() {

    boolean allowFlying = Minecraft.getMinecraft().player.capabilities.allowFlying;

    //ダブルジャンプ可能から不可に変更になった瞬間
    if (!allowFlying && this.allowFlying) {
      allowFlightChanged = System.currentTimeMillis();
    }
    //ダブルジャンプの状態を更新
    this.allowFlying = allowFlying;

    double cooltime = 10 - (System.currentTimeMillis() - allowFlightChanged) / 1000f;
    if (cooltime > 0) { return String.format(" %.2fsec", cooltime); }
    return " Ready!";
  }

}
