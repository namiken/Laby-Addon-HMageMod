package onimen.anni.hmage.event;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatReceiveEvent extends Event {
  private ITextComponent message;
  private final ChatType type;

  public ChatReceiveEvent(ChatType type, ITextComponent message) {
    this.type = type;
    this.setMessage(message);
  }

  public ITextComponent getMessage() {
    return message;
  }

  public void setMessage(ITextComponent message) {
    this.message = message;
  }

  public ChatType getType() {
    return type;
  }
}