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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.level.ServerLevel;

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
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslatableComponent("commands.forge.setdim.invalid.entity"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TranslatableComponent("commands.forge.setdim.invalid.dim", dim));
    static ArgumentBuilder<CommandSourceStack, ?> register()
    {
        return Commands.literal("setdimension")
            .requires(cs->cs.hasPermission(2)) //permission
            .then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("dim", DimensionArgument.dimension())
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> execute(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), DimensionArgument.getDimension(ctx, "dim"), BlockPosArgument.getSpawnablePos(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx, EntityArgument.getOptionalEntities(ctx, "targets"), DimensionArgument.getDimension(ctx, "dim"), new BlockPos(ctx.getSource().getPosition())))
                )
            );
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, Collection<? extends Entity> entities, ServerLevel dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(e -> !canEntityTeleport(e));
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        String cmdTarget = "@s";
        String posTarget = "";
        for (ParsedCommandNode<CommandSourceStack> parsed : ctx.getNodes())
        {
            final CommandNode<CommandSourceStack> node = parsed.getNode();
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
        final String dimName = dim.dimension().location().toString();
        final String finalCmdTarget = cmdTarget;
        final String finalPosTarget = posTarget;
        Component suggestion = new TranslatableComponent("/execute in %s run tp %s%s", dimName, cmdTarget, finalPosTarget)
                .withStyle((style) -> style.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in " + dimName + " run tp " + finalCmdTarget + finalPosTarget)));
        ctx.getSource().sendSuccess(new TranslatableComponent("commands.forge.setdim.deprecated", suggestion), true);

        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions();
    }
}
