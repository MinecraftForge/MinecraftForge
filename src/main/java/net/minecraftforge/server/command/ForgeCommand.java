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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class ForgeCommand
{
    public ForgeCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(
            LiteralArgumentBuilder.<CommandSource>literal("forge")
            .then(CommandTps.register())
            .then(CommandTrack.register())
            .then(CommandEntity.register())
            .then(CommandGenerate.register())
            .then(CommandDimensions.register())
            .then(CommandSetDimension.register())
            .then(CommandModList.register())
        );
    }
}
