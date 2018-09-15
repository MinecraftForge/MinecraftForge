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

package net.minecraftforge.server.command;

import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.Map;

import com.mojang.brigadier.builder.ArgumentBuilder;

public class CommandDimensions
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.func_197057_a("dimensions")
            .requires(cs->cs.func_197034_c(0)) //permission
            .executes(ctx -> {
                ctx.getSource().func_197030_a(new TextComponentTranslation("commands.forge.dimensions.list"), true);
                for (Map.Entry<DimensionType, IntSortedSet> entry : DimensionManager.getRegisteredDimensions().entrySet())
                {
                    ctx.getSource().func_197030_a(new TextComponentString(entry.getKey().getName() + ": " + entry.getValue()), true);
                }
                return 0;
            });
    }
}
