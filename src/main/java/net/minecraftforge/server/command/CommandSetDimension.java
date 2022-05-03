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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;

/** @deprecated For removal in 1.17, superseded by {@code /execute in <dim> run tp <targets>} */
@Deprecated
public class CommandSetDimension
{
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.setdim.invalid.entity"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TranslationTextComponent("commands.forge.setdim.invalid.dim", dim));
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("setdimension")
            .requires(cs->cs.hasPermission(2)) //permission
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("dim", DimensionArgument.dimension())
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> execute(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), DimensionArgument.getDimension(ctx, "dim"), BlockPosArgument.getOrLoadBlockPos(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), DimensionArgument.getDimension(ctx, "dim"), new BlockPos(ctx.getSource().getPosition())))
                )
            );
    }

    private static int execute(CommandContext<CommandSource> ctx, Collection<? extends Entity> entities, ServerWorld dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(e -> !canEntityTeleport(e));
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        String cmdTarget = "@s";
        String posTarget = "~ ~ ~";
        for (ParsedCommandNode<CommandSource> parsed : ctx.getNodes())
        {
            if (parsed.getNode() instanceof ArgumentCommandNode)
            {
                if ("targets".equals(parsed.getNode().getName()))
                {
                    cmdTarget = parsed.getRange().get(ctx.getInput());
                }
                else if ("pos".equals(parsed.getNode().getName()))
                {
                    posTarget = parsed.getRange().get(ctx.getInput());
                }
            }
        }
        final String dimName = dim.dimension().location().toString();
        final String finalCmdTarget = cmdTarget;
        final String finalPosTarget = posTarget;
        ITextComponent suggestion = new TranslationTextComponent("/execute in %s run tp %s %s", dimName, cmdTarget, finalPosTarget)
                .withStyle((style) -> style.withColor(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + dimName + " run tp " + finalCmdTarget + " " + finalPosTarget)));
        ctx.getSource().sendSuccess(new TranslationTextComponent("commands.forge.setdim.deprecated", suggestion), true);

        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions();
    }
}
