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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;
import java.util.function.Function;

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
                        .executes(ctx -> execute(ctx.getSource(), EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.getDimensionArgument(ctx, "dim"), BlockPosArgument.getBlockPos(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx.getSource(), EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.getDimensionArgument(ctx, "dim"), new BlockPos(ctx.getSource().getPos())))
                )
            );
    }

    private static int execute(CommandSource sender, Collection<? extends Entity> entities, ServerWorld dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(e -> !canEntityTeleport(e));
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        //if (!DimensionManager.isDimensionRegistered(dim))
        //    throw INVALID_DIMENSION.create(dim);

        entities.stream().filter(e -> e.world == dim).forEach(e -> sender.sendFeedback(new TranslationTextComponent("commands.forge.setdim.invalid.nochange", e.getDisplayName().getString(), dim), true));
        entities.stream().filter(e -> e.world != dim).forEach(e ->  e.changeDimension(dim , new ITeleporter()
        {
            @Override
            public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
            {
                Entity repositionedEntity = repositionEntity.apply(false);
                repositionedEntity.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
                return repositionedEntity;
            }
        }));


        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss();
    }
}
