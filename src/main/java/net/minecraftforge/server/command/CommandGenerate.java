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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.WorldWorkerManager;

class CommandGenerate
{
    static ArgumentBuilder<CommandSourceStack, ?> register()
    {
        return Commands.literal("generate")
            .requires(cs->cs.hasPermission(4)) //permission
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                    .then(Commands.argument("dim", DimensionArgument.dimension())
                        .then(Commands.argument("interval", IntegerArgumentType.integer())
                            .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.getDimension(ctx, "dim"), getInt(ctx, "interval")))
                        )
                        .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.getDimension(ctx, "dim"), -1))
                    )
                    .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getSpawnablePos(ctx, "pos"), getInt(ctx, "count"), ctx.getSource().getLevel(), -1))
                )
            );
    }

    private static int getInt(CommandContext<CommandSourceStack> ctx, String name)
    {
        return IntegerArgumentType.getInteger(ctx, name);
    }

    private static int execute(CommandSourceStack source, BlockPos pos, int count, ServerLevel dim, int interval) throws CommandRuntimeException
    {
        BlockPos chunkpos = new BlockPos(pos.getX() >> 4, 0, pos.getZ() >> 4);

        ChunkGenWorker worker = new ChunkGenWorker(source, chunkpos, count, dim, interval);
        source.sendSuccess(worker.getStartMessage(source), true);
        WorldWorkerManager.addWorker(worker);

        return 0;
    }
}
