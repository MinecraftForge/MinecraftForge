/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;

public class CommandSetDimension
{
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.setdim.invalid.entity"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TranslationTextComponent("commands.forge.setdim.invalid.dim", dim));
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("setdimension")
            .requires(cs->cs.hasPermissionLevel(2)) //permission
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("dim", DimensionArgument.getDimension())
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.func_212592_a(ctx, "dim"), BlockPosArgument.getBlockPos(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx.getSource(), EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.func_212592_a(ctx, "dim"), new BlockPos(ctx.getSource().getPos())))
                )
            );
    }

    private static int execute(CommandSource sender, Collection<? extends Entity> entities, DimensionType dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(e -> !canEntityTeleport(e));
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        //if (!DimensionManager.isDimensionRegistered(dim))
        //    throw INVALID_DIMENSION.create(dim);

        entities.stream().filter(e -> e.dimension == dim).forEach(e -> sender.sendFeedback(new TranslationTextComponent("commands.forge.setdim.invalid.nochange", e.getDisplayName().getFormattedText(), dim), true));
        entities.stream().filter(e -> e.dimension != dim).forEach(e ->  e.changeDimension(dim));

        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss();
    }
}
