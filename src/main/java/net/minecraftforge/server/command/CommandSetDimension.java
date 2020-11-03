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
            .requires(cs->cs.hasPermissionLevel(2)) //permission
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("dim", DimensionArgument.getDimension())
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> execute(ctx, EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.getDimensionArgument(ctx, "dim"), BlockPosArgument.getBlockPos(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx, EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.getDimensionArgument(ctx, "dim"), new BlockPos(ctx.getSource().getPos())))
                )
            );
    }

    private static int execute(CommandContext<CommandSource> ctx, Collection<? extends Entity> entities, ServerWorld dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(e -> !canEntityTeleport(e));
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        String cmdTarget = "@s";
        String posTarget = "";
        for (ParsedCommandNode<CommandSource> parsed : ctx.getNodes())
        {
            final CommandNode<CommandSource> node = parsed.getNode();
            if (parsed.getNode() instanceof ArgumentCommandNode)
            {
                if ("target".equals(parsed.getNode().getName()))
                {
                    cmdTarget = parsed.getRange().get(ctx.getInput());
                }
                else if ("pos".equals(parsed.getNode().getName()))
                {
                    posTarget = " " + parsed.getRange().get(ctx.getInput());
                }
            }
        }
        final String dimName = dim.getDimensionKey().getLocation().toString();
        final String finalCmdTarget = cmdTarget;
        final String finalPosTarget = posTarget;
        ITextComponent suggestion = new TranslationTextComponent("/execute in %s run tp %s%s", dimName, cmdTarget, finalPosTarget)
                .modifyStyle((style) -> style.setFormatting(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + dimName + " run tp " + finalCmdTarget + finalPosTarget)));
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.forge.setdim.deprecated", suggestion), true);

        //if (!DimensionManager.isDimensionRegistered(dim))
        //    throw INVALID_DIMENSION.create(dim);

        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss();
    }
}
