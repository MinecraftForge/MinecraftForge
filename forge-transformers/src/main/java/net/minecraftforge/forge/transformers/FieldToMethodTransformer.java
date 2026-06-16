/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.transformers;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

record FieldToMethodTransformer(String className, Map<String, String> fields) implements ITransformer<ClassNode> {
    private static final Logger LOGGER = LogManager.getLogger();

    // TODO [Forge][Transformer] Make this properly data driven or configurable.
    //      It was hard-coded like this before when using JS CoreMods, though.
    static final Map<String, Map<String, String>> TARGETS = Map.of(
        "net.minecraft.world.level.biome.Biome",
        Map.of(
            "climateSettings", "getModifiedClimateSettings",
            "specialEffects", "getModifiedSpecialEffects"
        ),

        "net.minecraft.world.effect.MobEffectInstance",
        Map.of(
            "effect", "getEffect"
        ),

        "net.minecraft.world.level.block.LiquidBlock",
        Map.of(
            "fluid", "getFluid"
        ),

        "net.minecraft.world.item.BucketItem",
        Map.of(
            "content", "getFluid"
        ),

        "net.minecraft.world.level.block.FlowerPotBlock",
        Map.of(
            "potted", "getPotted"
        )
    );

    @SuppressWarnings("rawtypes")
    static List<ITransformer> getAll() {
        var ret = new ArrayList<ITransformer>(TARGETS.size());
        for (var entry : TARGETS.entrySet()) {
            ret.add(new FieldToMethodTransformer(entry.getKey(), entry.getValue()));
        }
        return ret;
    }

    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        for (var entry : this.fields.entrySet()) {
            redirectFieldToMethod(input, entry.getKey(), entry.getValue());
        }

        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(Target.targetClass(this.className));
    }

    private static final @Nullable FieldNode findField(final ClassNode classNode, final String name) {
        FieldNode ret = null;
        for (var fieldNode : classNode.fields) {
            if (name.equals(fieldNode.name)) {
                if (ret != null)
                    throw new IllegalStateException("Found multiple fields with name " + name);
                ret = fieldNode;
            }
        }
        return ret;
    }

    private static final @Nullable MethodNode findMethod(final ClassNode classNode, final String name, final String desc) {
        for (var methodNode : classNode.methods) {
            if (name.equals(methodNode.name) && desc.equals(methodNode.desc))
                return methodNode;
        }
        return null;
    }

    /**
     * Rewrites accesses to a specific field in the given class to a method-call.
     * <p>
     * The field specified by fieldName must be private and non-static. The method-call the field-access is redirected
     * to does not take any parameters and returns an object of the same type as the field. If no methodName is passed,
     * any method matching the described signature will be used as callable method.
     *
     * @param classNode  the class to rewrite the accesses in
     * @param fieldName  the field accesses should be redirected to
     * @param methodName the name of the method to redirect accesses through
     * @apiNote This method was written as a special use case for Forge. It is not recommended to use this method
     *     unless you know what you are doing.
     */
    private static void redirectFieldToMethod(final ClassNode classNode, final String fieldName, final String methodName) {
        var foundField = findField(classNode, fieldName);
        if (foundField == null)
            throw new IllegalStateException("No field with name " + fieldName + " found");

        if (!Modifier.isPrivate(foundField.access) || Modifier.isStatic(foundField.access))
            throw new IllegalStateException("Field " + classNode.name + '.' +  fieldName + " is not private and an instance field");

        var methodSignature = "()" + foundField.desc;
        var foundMethod = findMethod(classNode, methodName, methodSignature);
        if (foundMethod == null)
            throw new IllegalStateException("Unable to find method " + methodName + methodSignature);

        for (MethodNode methodNode : classNode.methods) {
            // skip the found getter method
            if (methodNode == foundMethod) continue;

            // Why is this filtered? UI twas added in the beginning but would it matter?
            if (methodSignature.equals(foundMethod)) {
                LOGGER.debug("FieldToMethodTransformer " + classNode.name + " skipping " + methodNode.name + methodNode.hashCode());
                continue;
            }

            for (var insn : methodNode.instructions) {
                if (insn.getOpcode() != Opcodes.GETFIELD)
                    continue;

                var fieldInsnNode = (FieldInsnNode)insn;
                if (fieldName.equals(fieldInsnNode.name)) {
                    var replace = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, foundMethod.name, foundMethod.desc, false);
                    methodNode.instructions.set(insn, replace);
                }
            }
        }
    }
}
