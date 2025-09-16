/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.transformers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.coremod.api.ASMAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

// NOTE: THIS IS A SINGLETON!
record MethodRedirector() implements ITransformer<ClassNode> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    private static final Replacement[] REPLACEMENTS = {
        new Replacement(
            // finalizeSpawn redirection to ForgeEventFactory.onFinalizeSpawn
            ASMAPI.MethodType.VIRTUAL,
            "finalizeSpawn",
            "(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;",
            GSON.fromJson(new InputStreamReader(sneak(() -> MethodRedirector.class.getModule().getResourceAsStream("coremods/finalize_spawn_targets.json"))), Target[].class),
            insn -> ASMAPI.buildMethodCall(
                ASMAPI.MethodType.STATIC,
                "net/minecraftforge/event/ForgeEventFactory",
                "onFinalizeSpawn",
                "(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;"
            )
        )
    };

    private static <T> T sneak(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    record Replacement(
        ASMAPI.MethodType type,
        String name,
        String desc,
        Target[] targets,
        UnaryOperator<MethodInsnNode> factory
    ) { }

    record Target(
        @SerializedName("class") String className,
        String[] methods
    ) { }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<ITransformer.Target> targets() {
        return Arrays
            .stream(REPLACEMENTS)
            .map(Replacement::targets)
            .flatMap(Arrays::stream)
            .map(Target::className)
            .map(ITransformer.Target::targetClass)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public @NotNull ClassNode transform(ClassNode clazz, ITransformerVotingContext context) {
        for (var replacement : REPLACEMENTS) {
            for (var methodString : getClassTargetMethods(clazz, replacement)) {
                // cut up the string since we've put both the name and desc as the target
                var splitPos = methodString.indexOf('(');
                var methodName = methodString.substring(0, splitPos);
                var methodDesc = methodString.substring(splitPos);
                var method = ASMAPI.findMethodNode(clazz, methodName, methodDesc);

                if (method == null) {
                    LOGGER.error("Failed to redirect method call for {}! Method {} not found in class {}! This is a Forge bug, and is likely due to a Minecraft update changing something.", replacement.name, methodString, clazz.name);
                    continue;
                }

                for (var insn : method.instructions) {
                    if (!shouldReplace(insn, replacement)) continue;

                    var methodInsn = (MethodInsnNode) insn;
                    var redirection = replacement.factory.apply(methodInsn);
                    LOGGER.debug("Redirecting method call {}{} to {}{} inside of {}.{}", methodInsn.name, methodInsn.desc, redirection.name, redirection.desc, clazz.name, method.name);
                    ASMAPI.insertInsn(method, methodInsn, redirection, ASMAPI.InsertMode.REMOVE_ORIGINAL);
                }
            }
        }

        return clazz;
    }

    private static boolean shouldReplace(AbstractInsnNode insn, Replacement replacement) {
        return insn.getOpcode() == replacement.type.toOpcode()
            && replacement.name.equals(((MethodInsnNode) insn).name)
            && replacement.desc.equals(((MethodInsnNode) insn).desc);
    }

    private static List<String> getClassTargetMethods(ClassNode clazz, Replacement replacement) {
        for (var t : replacement.targets) {
            if (!t.className.equals(clazz.name)) continue;

            // declared methods
            var targets = new ArrayList<>(Arrays.asList(t.methods));

            // synthetic methods
            for (var method : clazz.methods) {
                if ((method.access & Opcodes.ACC_SYNTHETIC) != 0)
                    targets.add(method.name + method.desc);
            }

            return targets;
        }

        return List.of();
    }
}
