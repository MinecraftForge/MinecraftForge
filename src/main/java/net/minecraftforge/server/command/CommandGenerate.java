/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
            .requires(cs->cs.hasPermission(4)) //permission
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                    .then(Commands.argument("dim", DimensionArgument.dimension())
                        .then(Commands.argument("interval", IntegerArgumentType.integer())
                            .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getOrLoadBlockPos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.getDimension(ctx, "dim"), getInt(ctx, "interval")))
                        )
                        .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getOrLoadBlockPos(ctx, "pos"), getInt(ctx, "count"), DimensionArgument.getDimension(ctx, "dim"), -1))
                    )
                    .executes(ctx -> execute(ctx.getSource(), BlockPosArgument.getOrLoadBlockPos(ctx, "pos"), getInt(ctx, "count"), ctx.getSource().getLevel(), -1))
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
        source.sendSuccess(worker.getStartMessage(source), true);
        WorldWorkerManager.addWorker(worker);

        return 0;
    }
}
