/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.ForgeTimeTracker;

class TrackCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "track";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.usage.tracking";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
        String type = args[0];
        int duration = parseInt(args[1], 1, 60);

        if ("te".equals(type))
        {
            ForgeTimeTracker.tileEntityTrackingDuration = duration;
            ForgeTimeTracker.tileEntityTracking = true;
            sender.sendMessage(new TextComponentTranslation("commands.forge.tracking.te.enabled", duration));
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 0) {
            return Collections.singletonList("te");
        } else if (args.length == 1) {
            List<String> validNumbers = new ArrayList<>();
            for (int i = 1; i <= 60; i++) {
                validNumbers.add(Integer.toString(i));
            }
            return validNumbers;
        }
        return Collections.emptyList();
    }
}
