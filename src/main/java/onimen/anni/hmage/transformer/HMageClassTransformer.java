package onimen.anni.hmage.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class HMageClassTransformer implements IClassTransformer {

  @Override
  public byte[] transform(String name, String transformedName, byte[] bytes) {

    if ((!(name.equals("net.minecraft.client.network.NetHandlerPlayClient")))
        && (!(name.equals("brz")))) { return bytes; }

    System.out.println("[hmage asm]: transfer class:" + transformedName);

    try {

      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);

      for (MethodNode methodNode : (Iterable<MethodNode>) () -> classNode.methods.iterator()) {
        if (methodNode.desc.equals("(Lin;)V")) {
          new ActionBarHandleHook().injectHook(methodNode.instructions);
          System.out.println("[hmage asm]Inject ActionBarHandleHook");
        }
      }

      ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
      classNode.accept(classWriter);

      byte[] byteArray = classWriter.toByteArray();

      return byteArray;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}