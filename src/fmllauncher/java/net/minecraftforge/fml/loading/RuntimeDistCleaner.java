/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.collect.Streams;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class RuntimeDistCleaner implements ILaunchPluginService
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker DISTXFORM = MarkerManager.getMarker("DISTXFORM");
    private static String DIST;
    private static final String ONLYIN = Type.getDescriptor(OnlyIn.class);
    @Override
    public String name()
    {
        return "runtimedistcleaner";
    }

    @Override
    public void addResource(Path resource, String name)
    {

    }

    @Override
    public ClassNode processClass(ClassNode classNode, Type classType)
    {
        if (remove(classNode.visibleAnnotations, DIST))
        {
            LOGGER.debug(DISTXFORM, "Attempted to load class {} for invalid dist {}", classNode.name, DIST);
            throw new RuntimeException("Attempted to load class "+ classNode.name  + " for invalid dist "+ DIST);
        }

        Iterator<FieldNode> fields = classNode.fields.iterator();
        while(fields.hasNext())
        {
            FieldNode field = fields.next();
            if (remove(field.visibleAnnotations, DIST))
            {
                LOGGER.debug(DISTXFORM,"Removing field: {}.{}", classNode.name, field.name);
                fields.remove();
            }
        }

        LambdaGatherer lambdaGatherer = new LambdaGatherer();
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext())
        {
            MethodNode method = methods.next();
            if (remove(method.visibleAnnotations, DIST))
            {
                LOGGER.debug(DISTXFORM,"Removing method: {}.{}{}", classNode.name, method.name, method.desc);
                methods.remove();
                lambdaGatherer.accept(method);
            }
        }

        // remove dynamic lambda methods that are inside of removed methods
        List<Handle> dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles();
        if (!dynamicLambdaHandles.isEmpty())
        {
            methods = classNode.methods.iterator();
            while (methods.hasNext())
            {
                MethodNode method = methods.next();
                for (Handle dynamicLambdaHandle : dynamicLambdaHandles)
                {
                    if (method.name.equals(dynamicLambdaHandle.getName()) && method.desc.equals(dynamicLambdaHandle.getDesc()))
                    {
                        LOGGER.debug(DISTXFORM,"Removing lambda method: {}.{}{}", classNode.name, method.name, method.desc);
                        methods.remove();
                    }
                }
            }
        }
        return classNode;
    }

    private boolean remove(final List<AnnotationNode> anns, final String side)
    {
        return !(anns == null) && anns.stream().
                filter(ann->Objects.equals(ann.desc, ONLYIN)).
                anyMatch(ann -> !Objects.equals(((String[])ann.values.get(ann.values.indexOf("value")+1))[1], side));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Consumer<Dist> getExtension()
    {
        return (s)-> {
            DIST = s.name();
            LOGGER.debug(DISTXFORM, "Configuring for Dist {}", DIST);
        };
    }

    @Override
    public boolean handlesClass(Type classType, boolean isEmpty)
    {
        return !isEmpty;
    }

    private static class LambdaGatherer extends MethodVisitor {
        private static final Handle META_FACTORY = new Handle(Opcodes.H_INVOKESTATIC,
                "java/lang/invoke/LambdaMetafactory", "metafactory",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                false);
        private final List<Handle> dynamicLambdaHandles = new ArrayList<>();

        public LambdaGatherer() {
            super(Opcodes.ASM5);
        }

        public void accept(MethodNode method) {
            Streams.stream(method.instructions.iterator()).
                    filter(insnNode->insnNode.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN).
                    forEach(insnNode->insnNode.accept(this));
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
        {
            if (META_FACTORY.equals(bsm))
            {
                Handle dynamicLambdaHandle = (Handle) bsmArgs[1];
                dynamicLambdaHandles.add(dynamicLambdaHandle);
            }
        }

        public List<Handle> getDynamicLambdaHandles()
        {
            return dynamicLambdaHandles;
        }
    }
}
