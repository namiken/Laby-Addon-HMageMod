package onimen.anni.hmage.transformer;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChat;
import onimen.anni.hmage.anni.AnniChatReciveExecutor;
import onimen.anni.hmage.util.ShotbowUtils;

public class HMageHooks {

  public static void onChatMessage(SPacketChat packetIn) {
    if (ShotbowUtils.isShotbow(Minecraft.getMinecraft())) {
      AnniChatReciveExecutor.onReceiveChat(packetIn.getChatComponent().getUnformattedText());
    }
  }
}
