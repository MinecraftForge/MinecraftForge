package net.minecraftforge.fml.common.asm.transformers;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Type.VOID_TYPE;
import static org.objectweb.asm.Type.BOOLEAN_TYPE;
import static org.objectweb.asm.Type.getMethodDescriptor;

import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.eventhandler.Event;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EventSubscriptionTransformer implements IClassTransformer
{
    public EventSubscriptionTransformer()
    {
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null || name.equals("net.minecraftforge.fml.common.eventhandler.Event") || name.startsWith("net.minecraft.") || name.indexOf('.') == -1)
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
                ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
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

    private boolean buildEvents(ClassNode classNode) throws Exception
    {
        // Yes, this recursively loads classes until we get this base class. THIS IS NOT A ISSUE. Coremods should handle re-entry just fine.
        // If they do not this a COREMOD issue NOT a Forge/LaunchWrapper issue.
        Class<?> parent = this.getClass().getClassLoader().loadClass(classNode.superName.replace('/', '.'));
        if (!Event.class.isAssignableFrom(parent))
        {
            return false;
        }

        //Class<?> listenerListClazz = Class.forName("net.minecraftforge.fml.common.eventhandler.ListenerList", false, getClass().getClassLoader());
        Type tList = Type.getType("Lnet/minecraftforge/fml/common/eventhandler/ListenerList;");

        boolean edited             = false;
        boolean hasSetup           = false;
        boolean hasGetListenerList = false;
        boolean hasDefaultCtr      = false;
        boolean hasCancelable      = false;
        boolean hasResult          = false;
        String voidDesc            = Type.getMethodDescriptor(VOID_TYPE);
        String boolDesc            = Type.getMethodDescriptor(BOOLEAN_TYPE);
        String listDesc            = tList.getDescriptor();
        String listDescM           = Type.getMethodDescriptor(tList);

        for (MethodNode method : (List<MethodNode>)classNode.methods)
        {
            if (method.name.equals("setup") && method.desc.equals(voidDesc) && (method.access & ACC_PROTECTED) == ACC_PROTECTED) hasSetup = true;
            if ((method.access & ACC_PUBLIC) == ACC_PUBLIC)
            {
                if (method.name.equals("getListenerList") && method.desc.equals(listDescM)) hasGetListenerList = true;
                if (method.name.equals("isCancelable")    && method.desc.equals(boolDesc))  hasCancelable = true;
                if (method.name.equals("hasResult")       && method.desc.equals(boolDesc))  hasResult = true;
            }
            if (method.name.equals("<init>") && method.desc.equals(voidDesc)) hasDefaultCtr = true;
        }

        if (classNode.visibleAnnotations != null)
        {
            for (AnnotationNode node : classNode.visibleAnnotations)
            {
                if (!hasResult && node.desc.equals("Lnet/minecraftforge/fml/common/eventhandler/Event$HasResult;"))
                {
                    /* Add:
                     *      public boolean hasResult()
                     *      {
                     *            return true;
                     *      }
                     */
                    MethodNode method = new MethodNode(ACC_PUBLIC, "hasResult", boolDesc, null, null);
                    method.instructions.add(new InsnNode(ICONST_1));
                    method.instructions.add(new InsnNode(IRETURN));
                    classNode.methods.add(method);
                    edited = true;
                }
                else if (!hasCancelable && node.desc.equals("Lnet/minecraftforge/fml/common/eventhandler/Cancelable;"))
                {
                    /* Add:
                     *      public boolean isCancelable()
                     *      {
                     *            return true;
                     *      }
                     */
                    MethodNode method = new MethodNode(ACC_PUBLIC, "isCancelable", boolDesc, null, null);
                    method.instructions.add(new InsnNode(ICONST_1));
                    method.instructions.add(new InsnNode(IRETURN));
                    classNode.methods.add(method);
                    edited = true;
                }
            }
        }

        if (hasSetup)
        {
            if (!hasGetListenerList)
                throw new RuntimeException("Event class defines setup() but does not define getListenerList! " + classNode.name);
            else
                return edited;
        }

        Type tSuper = Type.getType(classNode.superName);

        //Add private static ListenerList LISTENER_LIST
        classNode.fields.add(new FieldNode(ACC_PRIVATE | ACC_STATIC, "LISTENER_LIST", listDesc, null, null));

        /*Add:
         *      public <init>()
         *      {
         *              super();
         *      }
         */
        if (!hasDefaultCtr)
        {
            MethodNode method = new MethodNode(ACC_PUBLIC, "<init>", voidDesc, null, null);
            method.instructions.add(new VarInsnNode(ALOAD, 0));
            method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tSuper.getInternalName(), "<init>", voidDesc, false));
            method.instructions.add(new InsnNode(RETURN));
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
        MethodNode method = new MethodNode(ACC_PROTECTED, "setup", voidDesc, null, null);
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tSuper.getInternalName(), "setup", voidDesc, false));
        method.instructions.add(new FieldInsnNode(GETSTATIC, classNode.name, "LISTENER_LIST", listDesc));
        LabelNode initLisitener = new LabelNode();
        method.instructions.add(new JumpInsnNode(IFNULL, initLisitener));
        method.instructions.add(new InsnNode(RETURN));
        method.instructions.add(initLisitener);
        method.instructions.add(new FrameNode(F_SAME, 0, null, 0, null));
        method.instructions.add(new TypeInsnNode(NEW, tList.getInternalName()));
        method.instructions.add(new InsnNode(DUP));
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tSuper.getInternalName(), "getListenerList", listDescM, false));
        method.instructions.add(new MethodInsnNode(INVOKESPECIAL, tList.getInternalName(), "<init>", getMethodDescriptor(VOID_TYPE, tList), false));
        method.instructions.add(new FieldInsnNode(PUTSTATIC, classNode.name, "LISTENER_LIST", listDesc));
        method.instructions.add(new InsnNode(RETURN));
        classNode.methods.add(method);

        /*Add:
         *      public ListenerList getListenerList()
         *      {
         *              return this.LISTENER_LIST;
         *      }
         */
        method = new MethodNode(ACC_PUBLIC, "getListenerList", listDescM, null, null);
        method.instructions.add(new FieldInsnNode(GETSTATIC, classNode.name, "LISTENER_LIST", listDesc));
        method.instructions.add(new InsnNode(ARETURN));
        classNode.methods.add(method);
        return true;
    }
}
