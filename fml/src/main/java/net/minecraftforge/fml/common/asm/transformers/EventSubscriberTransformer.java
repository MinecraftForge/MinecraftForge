package net.minecraftforge.fml.common.asm.transformers;

import java.lang.reflect.Modifier;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class EventSubscriberTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass == null) return null;

        ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);

        boolean isSubscriber = false;

        for (MethodNode methodNode : classNode.methods)
        {
            List<AnnotationNode> anns = methodNode.visibleAnnotations;

            if (anns != null && Iterables.any(anns, SubscribeEventPredicate.INSTANCE))
            {
                if (Modifier.isPrivate(methodNode.access))
                {
                    String msg = "Cannot apply @SubsribeEvent to private method %s/%s%s";
                    throw new RuntimeException(String.format(msg, classNode.name, methodNode.name, methodNode.desc));
                }

                methodNode.access = toPublic(methodNode.access);
                isSubscriber = true;
            }
        }

        if (isSubscriber)
        {
            classNode.access = toPublic(classNode.access);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    private static int toPublic(int access)
    {
        return access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
    }

    private static class SubscribeEventPredicate implements Predicate<AnnotationNode>
    {
        static final SubscribeEventPredicate INSTANCE = new SubscribeEventPredicate();

        @Override
        public boolean apply(AnnotationNode input)
        {
            return input.desc.equals("Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;");
        }
    }
}
