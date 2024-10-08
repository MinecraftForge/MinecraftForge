/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
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
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class RuntimeDistCleaner implements ILaunchPluginService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Marker DISTXFORM = MarkerFactory.getMarker("DISTXFORM");
    private static String DIST;
    private static final String ONLYIN = Type.getDescriptor(OnlyIn.class);
    private static final String ONLYINS = Type.getDescriptor(OnlyIns.class);
    private static final String MOD = "Lnet/minecraftforge/fml/common/Mod;";

    @Override
    public String name() {
        return "runtimedistcleaner";
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason) {
        var changed = false;

        var annotations = unpack(classNode.visibleAnnotations);

        if (remove(annotations, DIST)) {
            LOGGER.error(DISTXFORM, "Attempted to load class {} for invalid dist {}", classNode.name, DIST);
            throw new RuntimeException("Attempted to load class "+ classNode.name  + " for invalid dist "+ DIST);
        }

        if (!FMLEnvironment.production && !annotations.isEmpty() && hasModAnnotation(classNode.visibleAnnotations)) {
            LOGGER.error(DISTXFORM, "Attempted to load class {} with @Mod and @OnlyIn/@OnlyIns annotations", classNode.name);
            throw new RuntimeException("Found @OnlyIn on @Mod class "+ classNode.name  + " - this is not allowed as it causes crashes. Remove the OnlyIn and consider setting clientSideOnly=true in the root of your mods.toml instead");
        }

        if (classNode.interfaces != null && !classNode.interfaces.isEmpty()) {
            for (var ann : annotations) {
                if (ann.intf == null || DIST.equals(ann.side))
                    continue;

                if (classNode.interfaces.remove(ann.intf)) {
                    LOGGER.debug(DISTXFORM,"Removing Interface: {} implements {}", classNode.name, ann.intf);
                    changed = true;
                }
            }

            //Remove Class level @OnlyIn/@OnlyIns annotations, this is important if anyone gets ambitious and tries to reflect an annotation with _interface set.
            if (classNode.visibleAnnotations != null) {
                for (var itr = classNode.visibleAnnotations.iterator(); itr.hasNext(); ) {
                    var ann = itr.next();
                    if (ONLYIN.equals(ann.desc) || ONLYINS.equals(ann.desc)) {
                        LOGGER.debug(DISTXFORM, "Removing Class Annotation: {} @{}", classNode.name, ann.desc);
                        itr.remove();
                        changed = true;
                    }
                }
            }
        }

        for (var itr = classNode.fields.iterator(); itr.hasNext(); ) {
            var field = itr.next();
            if (remove(unpack(field.visibleAnnotations), DIST)) {
                LOGGER.debug(DISTXFORM, "Removing field: {}.{}", classNode.name, field.name);
                itr.remove();
                changed = true;
            }
        }

        var lambdaGatherer = new LambdaGatherer();
        for (var itr = classNode.methods.iterator(); itr.hasNext(); ) {
            var method = itr.next();
            if (remove(unpack(method.visibleAnnotations), DIST)) {
                LOGGER.debug(DISTXFORM, "Removing method: {}.{}{}", classNode.name, method.name, method.desc);
                itr.remove();
                lambdaGatherer.accept(method);
                changed = true;
            }
        }

        // remove dynamic synthetic lambda methods that are inside of removed methods
        for (List<Handle> handles = lambdaGatherer.getDynamicLambdaHandles();
            !handles.isEmpty(); handles = lambdaGatherer.getDynamicLambdaHandles()) {
            lambdaGatherer = new LambdaGatherer();
            for (var itr = classNode.methods.iterator(); itr.hasNext(); ) {
                MethodNode method = itr.next();
                if ((method.access & Opcodes.ACC_SYNTHETIC) == 0)
                    continue;

                for (var handle : handles) {
                    if (method.name.equals(handle.getName()) && method.desc.equals(handle.getDesc())) {
                        LOGGER.debug(DISTXFORM, "Removing lambda method: {}.{}{}", classNode.name, method.name, method.desc);
                        itr.remove();
                        lambdaGatherer.accept(method);
                        changed = true;
                    }
                }
            }
        }

        return changed ? ComputeFlags.SIMPLE_REWRITE : ComputeFlags.NO_REWRITE;
    }

    private static record Target(String side, String intf) {
        static Target from(final AnnotationNode node) {
            var idx = node.values.indexOf("value");
            var value = (String[])node.values.get(idx + 1); // Enums are stored as [Type, Value]

            idx = node.values.indexOf("_interface");
            if (idx == -1)
                return new Target(value[1], null);

            var intf = (Type)node.values.get(idx + 1);
            return new Target(value[1], intf.getInternalName());
        }
    }

    /**
     * Unpack any OnlyIn or OnlyIns annotations to Target objects.
     */
    @SuppressWarnings("unchecked")
    private static List<Target> unpack(final List<AnnotationNode> anns) {
        if (anns == null || anns.isEmpty())
            return Collections.emptyList();

        var ret = new ArrayList<Target>();
        for (var node : anns) {
            if (ONLYIN.equals(node.desc))
                ret.add(Target.from(node));
            else if (ONLYINS.equals(node.desc)) {
                if (node.values == null || node.values.isEmpty())
                    continue;

                int idx = node.values.indexOf("value");
                if (idx == -1)
                    continue;

                var subNodes = (List<AnnotationNode>)node.values.get(idx + 1);
                if (subNodes != null) {
                    for (var sub : subNodes)
                        ret.add(Target.from(sub));
                }
            }
        }

        return ret;
    }

    private static boolean remove(final List<Target> targets, final String side) {
        for (var target : targets) {
            if (target.intf == null && !side.equals(target.side))
                return true;
        }

        return false;
    }

    private static boolean hasModAnnotation(final List<AnnotationNode> anns) {
        if (anns == null || anns.isEmpty())
            return false;

        for (var ann : anns) {
            if (ann.desc.equals(MOD))
                return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Consumer<Dist> getExtension() {
        return s -> {
            DIST = s.name();
            LOGGER.debug(DISTXFORM, "Configuring for Dist {}", DIST);
        };
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
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
            for (var insn : method.instructions) {
                if (insn.getType() == AbstractInsnNode.INVOKE_DYNAMIC_INSN)
                    insn.accept(this);
            }
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
            if (META_FACTORY.equals(bsm)) {
                var handle = (Handle)bsmArgs[1];
                dynamicLambdaHandles.add(handle);
            }
        }

        public List<Handle> getDynamicLambdaHandles() {
            return dynamicLambdaHandles;
        }
    }
}
