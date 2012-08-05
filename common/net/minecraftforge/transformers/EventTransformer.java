package net.minecraftforge.transformers;

import java.util.List;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.ListenerList;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;
import static org.objectweb.asm.ClassWriter.*;

import cpw.mods.fml.relauncher.IClassTransformer;

public class EventTransformer implements IClassTransformer
{
    public EventTransformer()
    {
    }
    
    @Override
    public byte[] transform(String name, byte[] bytes)
    {        
        ClassReader cr = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);
        
        try
        {
            buildEvents(classNode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        classNode.accept(cw);
        
        return cw.toByteArray();
    }
    
    @SuppressWarnings("unchecked")
    private void buildEvents(ClassNode classNode) throws Exception
    {
        Class<?> parent = Class.forName(classNode.superName.replace('/', '.'));
        if (!Event.class.isAssignableFrom(parent))
        {
                return;
        }
        
        boolean hasSetup = false;
        boolean hasGetListenerList = false;
        
        Type tList = Type.getType(ListenerList.class);
        
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
        }
        
        if (hasSetup)
        {
                if (!hasGetListenerList)
                {
                        throw new RuntimeException("Event class defines setup() but does not define getListenerList! " + classNode.name);
                }
                else
                {
                        return;
                }
        }
        
        Type tSuper = Type.getType(classNode.superName);
        
        //Add private static ListenerList LISTENER_LIST
        classNode.fields.add(new FieldNode(ACC_PRIVATE | ACC_STATIC, "LISTENER_LIST", tList.getDescriptor(), null, null));
        
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
        MethodNode method = new MethodNode(ASM4, ACC_PROTECTED, "setup", getMethodDescriptor(VOID_TYPE), null, null);
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
    }

}
