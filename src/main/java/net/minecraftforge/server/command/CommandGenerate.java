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

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.WorldWorkerManager;

class CommandGenerate
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("generate")
            .requires(cs->cs.hasPermissionLevel(4)) //permission
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                    .then(Commands.argument("dim", DimensionArgument.getDimension())
                        .then(Commands.argument("interval", IntegerArgumentType.integer())
                            .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.getDimensionArgument(ctx, "dim"), getInt(ctx, "interval")))
                        )
                        .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.getDimensionArgument(ctx, "dim"), -1))
                    )
                    .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), getInt(ctx, "count"), ctx.getSource().getWorld(), -1))
                )
            );
    }

    private static int getInt(CommandContext<CommandSource> ctx, String name)
    {
        return IntegerArgumentType.getInteger(ctx, name);
    }

    private static int execute(CommandSource source, BlockPos pos, int count, ServerWorld dim, int interval) throws CommandException
    {
        BlockPos chunkpos = new BlockPos(pos.getX() >> 4, 0, pos.getZ() >> 4);

        ChunkGenWorker worker = new ChunkGenWorker(source, chunkpos, count, dim, interval);
        source.sendFeedback(worker.getStartMessage(source), true);
        WorldWorkerManager.addWorker(worker);

        return 0;
    }
}
