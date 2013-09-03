package net.minecraftforge.transformers;

import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ListenerList;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;
import static org.objectweb.asm.ClassWriter.*;

public class EventTransformer implements IClassTransformer
{
    public EventTransformer()
    {
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null || name.equals("net.minecraftforge.event.Event") || name.startsWith("net.minecraft.") || name.indexOf('.') == -1)
        {
            return bytes;
        }
        ClassReader cr = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        try
        {
            if (buildEvents(classNode))
            {
                ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
                classNode.accept(cw);
                return cw.toByteArray();
            }
            return bytes;
        }
        catch (ClassNotFoundException ex)
        {
            // Discard silently- it's just noise
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bytes;
    }

    @SuppressWarnings("unchecked")
    private boolean buildEvents(ClassNode classNode) throws Exception
    {
        Class<?> parent = this.getClass().getClassLoader().loadClass(classNode.superName.replace('/', '.'));
        if (!Event.class.isAssignableFrom(parent))
        {
            return false;
        }

        boolean hasSetup = false;
        boolean hasGetListenerList = false;
        boolean hasDefaultCtr = false;

        Class<?> listenerListClazz = Class.forName("net.minecraftforge.event.ListenerList", false, getClass().getClassLoader());
        Type tList = Type.getType(listenerListClazz);

        for (MethodNode method : (List<MethodNode>)classNode.methods)
        {
                if (method.name.equals("setup") &&
                    method.desc.equals(Type.getMethodDescriptor(VOID_TYPE)) &&
                    (method.access & ACC_PROTECTED) == ACC_PROTECTED)
                {
                    hasSetup = true;
                }
                if (method.name.equals("getListenerList") &&
                    method.desc.equals(Type.getMethodDescriptor(tList)) &&
                    (method.access & ACC_PUBLIC) == ACC_PUBLIC)
                {
                    hasGetListenerList = true;
                }
                if (method.name.equals("<init>") &&
                    method.desc.equals(Type.getMethodDescriptor(VOID_TYPE)))
                {
                    hasDefaultCtr = true;
                }
        }

        if (hasSetup)
        {
                if (!hasGetListenerList)
                {
                        throw new RuntimeException("Event class defines setup() but does not define getListenerList! " + classNode.name);
                }
                else
                {
                        return false;
                }
        }

        Type tSuper = Type.getType(classNode.superName);

        //Add private static ListenerList LISTENER_LIST
        classNode.fields.add(new FieldNode(ACC_PRIVATE | ACC_STATIC, "LISTENER_LIST", tList.getDescriptor(), null, null));

        /*Add:
         *      public <init>()
         *      {
         *              super();
         *      }
         */
        MethodNode method = new MethodNode(ASM4, ACC_PUBLIC, "<init>", getMethodDescriptor(VOID_TYPE), null, null);
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tSuper.getInternalName(), "<init>", getMethodDescriptor(VOID_TYPE)));
        method.instructions.add(new InsnNode(RETURN));
        if (!hasDefaultCtr)
        {
            classNode.methods.add(method);
        }

        /*Add:
         *      protected void setup()
         *      {
         *              super.setup();
         *              if (LISTENER_LIST != NULL)
         *              {
         *                      return;
         *              }
         *              LISTENER_LIST = new ListenerList(super.getListenerList());
         *      }
         */
        method = new MethodNode(ASM4, ACC_PROTECTED, "setup", getMethodDescriptor(VOID_TYPE), null, null);
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tSuper.getInternalName(), "setup", getMethodDescriptor(VOID_TYPE)));
        method.instructions.add(new FieldInsnNode(GETSTATIC, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
        LabelNode initLisitener = new LabelNode();
        method.instructions.add(new JumpInsnNode(IFNULL, initLisitener));
        method.instructions.add(new InsnNode(RETURN));
        method.instructions.add(initLisitener);
        method.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        method.instructions.add(new TypeInsnNode(NEW, tList.getInternalName()));
        method.instructions.add(new InsnNode(DUP));
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tSuper.getInternalName(), "getListenerList", getMethodDescriptor(tList)));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tList.getInternalName(), "<init>", getMethodDescriptor(VOID_TYPE, tList)));
        method.instructions.add(new FieldInsnNode(PUTSTATIC, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
        method.instructions.add(new InsnNode(RETURN));
        classNode.methods.add(method);

        /*Add:
         *      public ListenerList getListenerList()
         *      {
         *              return this.LISTENER_LIST;
         *      }
         */
        method = new MethodNode(ASM4, ACC_PUBLIC, "getListenerList", getMethodDescriptor(tList), null, null);
        method.instructions.add(new FieldInsnNode(GETSTATIC, classNode.name, "LISTENER_LIST", tList.getDescriptor()));
        method.instructions.add(new InsnNode(ARETURN));
        classNode.methods.add(method);
        return true;
    }
}
