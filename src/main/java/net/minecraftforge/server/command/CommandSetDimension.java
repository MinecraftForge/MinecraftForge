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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandSetDimension extends CommandBase
{
    @Override
    public String getName()
    {
        return "setdimension";
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("setdim");
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.setdim.usage";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length > 2 && args.length <= 5)
        {
            return getTabCompletionCoordinate(args, 2, targetPos);
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        // args: <entity> <dim> [<x> <y> <z>]
        if (args.length != 2 && args.length != 5)
        {
            throw new WrongUsageException("commands.forge.setdim.usage");
        }
        Entity entity = getEntity(server, sender, args[0]);
        if (!checkEntity(entity))
        {
            throw new CommandException("commands.forge.setdim.invalid.entity", entity.getName());
        }
        int dimension = parseInt(args[1]);
        if (!DimensionManager.isDimensionRegistered(dimension))
        {
            throw new CommandException("commands.forge.setdim.invalid.dim", dimension);
        }
        if (dimension == entity.dimension)
        {
            throw new CommandException("commands.forge.setdim.invalid.nochange", entity.getName(), dimension);
        }
        BlockPos pos = args.length == 5 ? parseBlockPos(sender, args, 2, false) : sender.getPosition();
        entity.changeDimension(dimension, new CommandTeleporter(pos));
    }

    private static boolean checkEntity(Entity entity)
    {
        // use vanilla portal logic, try to avoid doing anything too silly
        return !entity.isRiding() && !entity.isBeingRidden() && entity.isNonBoss();
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
