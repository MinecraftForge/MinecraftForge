/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.brigadier.builder.ArgumentBuilder;

public class CommandDimensions
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("dimensions")
            .requires(cs->cs.hasPermission(0)) //permission
            .executes(ctx -> {
                ctx.getSource().sendSuccess(new TranslationTextComponent("commands.forge.dimensions.list"), true);
                final Registry<DimensionType> reg = ctx.getSource().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);

                Map<ResourceLocation, List<ResourceLocation>> types = new HashMap<>();
                for (ServerWorld dim : ctx.getSource().getServer().getAllLevels()) {
                    types.computeIfAbsent(reg.getKey(dim.dimensionType()), k -> new ArrayList<>()).add(dim.dimension().location());
                }

                types.keySet().stream().sorted().forEach(key -> {
                    ctx.getSource().sendSuccess(new StringTextComponent(key + ": " + types.get(key).stream().map(ResourceLocation::toString).sorted().collect(Collectors.joining(", "))), false);
                });
                return 0;
            });
    }
}
