package onimen.anni.hmage.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.network.play.server.SPacketChat;

public class ActionBarHandleHook {

  public void injectHook(InsnList list) {
    InsnList injectings = new InsnList();

    MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "onimen/anni/hmage/transformer/HMageHooks",
        "onChatMessage", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(SPacketChat.class)), false);

    injectings.add(new VarInsnNode(Opcodes.ALOAD, 1));
    injectings.add(hook);

    list.insert(injectings);
  }

}
