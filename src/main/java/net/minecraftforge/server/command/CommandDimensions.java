/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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
            .requires(cs->cs.hasPermissionLevel(0)) //permission
            .executes(ctx -> {
                ctx.getSource().sendFeedback(new TranslationTextComponent("commands.forge.dimensions.list"), true);
                final Registry<DimensionType> reg = ctx.getSource().func_241861_q().getRegistry(Registry.DIMENSION_TYPE_KEY);

                Map<ResourceLocation, List<ResourceLocation>> types = new HashMap<>();
                for (ServerWorld dim : ctx.getSource().getServer().getWorlds()) {
                    types.computeIfAbsent(reg.getKey(dim.getDimensionType()), k -> new ArrayList<>()).add(dim.getDimensionKey().getLocation());
                }

                types.keySet().stream().sorted().forEach(key -> {
                    ctx.getSource().sendFeedback(new StringTextComponent(key + ": " + types.get(key).stream().map(ResourceLocation::toString).sorted().collect(Collectors.joining(", "))), false);
                });
                return 0;
            });
    }
}
