/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import java.util.ArrayList;
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
            .requires(cs->cs.hasPermissionLevel(0)) //permission
            .executes(ctx -> {
                ctx.getSource().sendFeedback(new TranslationTextComponent("commands.forge.dimensions.list"), true);
                Map<String, List<String>> types = new HashMap<>();
                for (DimensionType dim : DimensionType.getAll()) {
                    String key = dim.getModType() == null ? "Vanilla" : dim.getModType().getRegistryName().toString();
                    types.computeIfAbsent(key, k -> new ArrayList<>()).add(DimensionType.getKey(dim).toString());
                }

                types.keySet().stream().sorted().forEach(key -> {
                    ctx.getSource().sendFeedback(new StringTextComponent(key + ": " + types.get(key).stream().sorted().collect(Collectors.joining(", "))), true);
                });
                return 0;
            });
    }
}
