/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.transformers;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraftforge.coremod.api.ASMAPI;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

record FieldToMethodTransformer(String className, Map<String, String> fields) implements ITransformer<ClassNode> {
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
            ASMAPI.redirectFieldToMethod(input, entry.getKey(), entry.getValue());
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
}
