/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
                /*
                Map<String, List<String>> types = new HashMap<>();
                for (DimensionType dim : DimensionType.getAll()) {
                    String key = dim.getModType() == null ? "Vanilla" : dim.getModType().getRegistryName().toString();
                    types.computeIfAbsent(key, k -> new ArrayList<>()).add(DimensionType.getKey(dim).toString());
                }

                types.keySet().stream().sorted().forEach(key -> {
                    ctx.getSource().sendFeedback(new StringTextComponent(key + ": " + types.get(key).stream().sorted().collect(Collectors.joining(", "))), false);
                });
                */
                ctx.getSource().getServer().func_240770_D_().stream().sorted().forEach(key -> {
                	ctx.getSource().sendFeedback(new StringTextComponent(key.func_240901_a_().toString()), false);
                });
                return 0;
            });
    }
}
