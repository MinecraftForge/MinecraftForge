/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.OnlyIns;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class RuntimeDistCleaner implements ILaunchPluginService
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Marker DISTXFORM = MarkerFactory.getMarker("DISTXFORM");
    private static String DIST;
    private static final String ONLYIN = Type.getDescriptor(OnlyIn.class);
    private static final String ONLYINS = Type.getDescriptor(OnlyIns.class);
    @Override
    public String name()
    {
        return "runtimedistcleaner";
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        AtomicBoolean changes = new AtomicBoolean();
        if (remove(classNode.visibleAnnotations, DIST))
        {
            LOGGER.error(DISTXFORM, "Attempted to load class {} for invalid dist {}", classNode.name, DIST);
            throw new RuntimeException("Attempted to load class "+ classNode.name  + " for invalid dist "+ DIST);
        }

        if (!FMLEnvironment.production && hasOnlyInWithModAnnotation(classNode.visibleAnnotations))
        {
            LOGGER.error(DISTXFORM, "Attempted to load class {} with @Mod and @OnlyIn/@OnlyIns annotations", classNode.name);
            throw new RuntimeException("Found @OnlyIn on @Mod class "+ classNode.name  + " - this is not allowed as it causes crashes. Remove the OnlyIn and consider setting clientSideOnly=true in the root of your mods.toml instead");
        }

        if (classNode.interfaces != null )
        {
            unpack(classNode.visibleAnnotations).stream()
                .filter(ann->Objects.equals(ann.desc, ONLYIN))
                .filter(ann->ann.values.indexOf("_interface") != -1)
                .filter(ann->!Objects.equals(((String[])ann.values.get(ann.values.indexOf("value") + 1))[1], DIST))
                .map(ann -> ((Type)ann.values.get(ann.values.indexOf("_interface") + 1)).getInternalName())
                .forEach(intf -> {
                    if (classNode.interfaces.remove(intf)) {
                        LOGGER.debug(DISTXFORM,"Removing Interface: {} implements {}", classNode.name, intf);
                        changes.compareAndSet(false, true);
                    }
                });

            //Remove Class level @OnlyIn/@OnlyIns annotations, this is important if anyone gets ambitious and tries to reflect an annotation with _interface set.
            if (classNode.visibleAnnotations != null) {
                Iterator<AnnotationNode> itr = classNode.visibleAnnotations.iterator();
                while (itr.hasNext()) {
                    AnnotationNode ann = itr.next();
                    if (Objects.equals(ann.desc, ONLYIN) || Objects.equals(ann.desc, ONLYINS)) {
                        LOGGER.debug(DISTXFORM,"Removing Class Annotation: {} @{}", classNode.name, ann.desc);
                        itr.remove();
                        changes.compareAndSet(false, true);
                    }
                }
            }
        }

        Iterator<FieldNode> fields = classNode.fields.iterator();
        while(fields.hasNext())
        {
            FieldNode field = fields.next();
            if (remove(field.visibleAnnotations, DIST))
            {
                LOGGER.debug(DISTXFORM,"Removing field: {}.{}", classNode.name, field.name);
                fields.remove();
                changes.compareAndSet(false, true);
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
                changes.compareAndSet(false, true);
            }
        }

        // remove dynamic synthetic lambda methods that are inside of removed methods
        for (List<Handle> dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles();
            !dynamicLambdaHandles.isEmpty(); dynamicLambdaHandles = lambdaGatherer.getDynamicLambdaHandles())
        {
            lambdaGatherer = new LambdaGatherer();
            methods = classNode.methods.iterator();
            while (methods.hasNext())
            {
                MethodNode method = methods.next();
                if ((method.access & Opcodes.ACC_SYNTHETIC) == 0) continue;
                for (Handle dynamicLambdaHandle : dynamicLambdaHandles)
                {
                    if (method.name.equals(dynamicLambdaHandle.getName()) && method.desc.equals(dynamicLambdaHandle.getDesc()))
                    {
                        LOGGER.debug(DISTXFORM,"Removing lambda method: {}.{}{}", classNode.name, method.name, method.desc);
                        methods.remove();
                        lambdaGatherer.accept(method);
                        changes.compareAndSet(false, true);
                    }
                }
            }
        }
        return changes.get() ? ComputeFlags.SIMPLE_REWRITE : ComputeFlags.NO_REWRITE;
    }

    @SuppressWarnings("unchecked")
    private static List<AnnotationNode> unpack(final List<AnnotationNode> anns)
    {
        if (anns == null)
        {
            return Collections.emptyList();
        }

        List<AnnotationNode> unpacked = new ArrayList<>();

        for (var annotationNode : anns)
        {
            if (Objects.equals(annotationNode.desc, ONLYINS))
            {
                unpacked.add(annotationNode);

                if (annotationNode.values != null)
                {
                    List<AnnotationNode> subNodes = (List<AnnotationNode>) annotationNode.values.get(annotationNode.values.indexOf("value") + 1);

                    if (subNodes != null)
                    {
                        unpacked.addAll(subNodes);
                    }
                }
            }
        }

        return unpacked;
    }

    private boolean remove(final List<AnnotationNode> anns, final String side)
    {
        var onlyIns = unpack(anns);

        for (var onlyIn : onlyIns)
        {
            if (!Objects.equals(onlyIn.desc, ONLYINS) || onlyIn.values.contains("_interface"))
            {
                continue;
            }

            if (!Objects.equals(((String[])onlyIn.values.get(onlyIn.values.indexOf("value") + 1))[1], side))
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasOnlyInWithModAnnotation(final List<AnnotationNode> anns)
    {
        if (anns == null)
        {
            return false;
        }

        var foundModAnnotation = false;
        var foundOnlyIn = false;

        for (var ann : anns)
        {
            if (ann.desc.equals("Lnet/minecraftforge/fml/common/Mod;"))
            {
                foundModAnnotation = true;
            } else if (Objects.equals(ann.desc, ONLYIN) || Objects.equals(ann.desc, ONLYINS))
            {
                foundOnlyIn = true;
            }
        }

        return foundModAnnotation && foundOnlyIn;
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

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return isEmpty ? NAY : YAY;
    }

    private static class LambdaGatherer extends MethodVisitor {
        private static final Handle META_FACTORY = new Handle(Opcodes.H_INVOKESTATIC,
                "java/lang/invoke/LambdaMetafactory", "metafactory",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                false);
        private final List<Handle> dynamicLambdaHandles = new ArrayList<>();

        public LambdaGatherer() {
            super(Opcodes.ASM9);
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
