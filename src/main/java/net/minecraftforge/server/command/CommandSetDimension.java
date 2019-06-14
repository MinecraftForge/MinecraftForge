/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ITeleporter;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;
import java.util.function.Predicate;

public class CommandSetDimension
{
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forge.setdim.invalid.entity"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TextComponentTranslation("commands.forge.setdim.invalid.dim", dim));
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("setdimension")
            .requires(cs->cs.hasPermissionLevel(2)) //permission
            .then(Commands.argument("targets", EntityArgument.multipleEntities())
                .then(Commands.argument("dim", DimensionArgument.func_212595_a())
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

        final ITeleporter teleporter = new CommandTeleporter(pos);
        entities.stream().filter(e -> e.dimension == dim).forEach(e -> sender.sendFeedback(new TextComponentTranslation("commands.forge.setdim.invalid.nochange", e.getDisplayName(), dim), true));
        entities.stream().filter(e -> e.dimension != dim).forEach(e -> e.changeDimension(dim, teleporter));

        return 0;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        // use vanilla portal logic from BlockPortal#onEntityCollision
        return !entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss();
    }

    private static class CommandTeleporter implements ITeleporter
    {
        private final BlockPos targetPos;

        private CommandTeleporter(BlockPos targetPos)
        {
            this.targetPos = targetPos;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw)
        {
            entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
        }
    }
}
