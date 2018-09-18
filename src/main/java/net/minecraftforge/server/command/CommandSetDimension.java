/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import it.unimi.dsi.fastutil.ints.IntSortedSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandSetDimension
{
    private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forge.setdim.invalid.entity"));
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TextComponentTranslation("commands.forge.setdim.invalid.dim", dim));
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.func_197057_a("setdimension")
            .requires(cs->cs.func_197034_c(2)) //permission
            .then(Commands.func_197056_a("targets", EntityArgument.func_197093_b())
                .then(Commands.func_197056_a("dim", IntegerArgumentType.integer())
                    .suggests((ctx, builder) -> ISuggestionProvider.func_197013_a(DimensionManager.getIDStream().sorted().map(id -> id.toString()), builder))
                    .then(Commands.func_197056_a("pos", BlockPosArgument.func_197276_a())
                        .executes(ctx -> execute(ctx.getSource(), EntityArgument.func_197087_c(ctx, "targets"), IntegerArgumentType.getInteger(ctx, "dim"), BlockPosArgument.func_197274_b(ctx, "pos")))
                    )
                    .executes(ctx -> execute(ctx.getSource(), EntityArgument.func_197087_c(ctx, "targets"), IntegerArgumentType.getInteger(ctx, "dim"), new BlockPos(ctx.getSource().func_197036_d())))
                )
            );
    }

    private static int execute(CommandSource sender, Collection<? extends Entity> entities, int dim, BlockPos pos) throws CommandSyntaxException
    {
        entities.removeIf(CommandSetDimension::checkEntity);
        if (entities.isEmpty())
            throw NO_ENTITIES.create();

        if (!DimensionManager.isDimensionRegistered(dim))
            throw INVALID_DIMENSION.create(dim);

        final ITeleporter teleporter = new CommandTeleporter(pos);
        entities.stream().filter(e -> e.dimension == dim).forEach(e -> sender.func_197030_a(new TextComponentTranslation("commands.forge.setdim.invalid.nochange", e.getDisplayName(), dim), true));
        entities.stream().filter(e -> e.dimension != dim).forEach(e -> e.changeDimension(dim, teleporter));

        return 0;
    }

    private static boolean checkEntity(Entity entity)
    {
        // use vanilla portal logic, try to avoid doing anything too silly
        return entity.isRiding() || entity.isBeingRidden() || entity.isNonBoss();
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
