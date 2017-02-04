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

import java.text.DecimalFormat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.ForgeTimeTracker;

public class ForgeCommand extends CommandBase {

    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

    @Override
    public String getName()
    {
        return "forge";
    }

    @Override
    public String getUsage(ICommandSender icommandsender)
    {
        return "commands.forge.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("commands.forge.usage");
        }
        else if ("help".equals(args[0]))
        {
            throw new WrongUsageException("commands.forge.usage");
        }
        else if ("tps".equals(args[0]))
        {
            displayTPS(server, sender,args);
        }
        else if ("tpslog".equals(args[0]))
        {
            doTPSLog(server, sender,args);
        }
        else if ("track".equals(args[0]))
        {
            handleTracking(server, sender, args);
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage");
        }
    }

    private void handleTracking(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 3)
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
        String type = args[1];
        int duration = parseInt(args[2], 1, 60);

        if ("te".equals(type))
        {
            doTurnOnTileEntityTracking(server, sender, duration);
        }
        else
        {
            throw new WrongUsageException("commands.forge.usage.tracking");
        }
    }

    private void doTurnOnTileEntityTracking(MinecraftServer server, ICommandSender sender, int duration)
    {
        ForgeTimeTracker.tileEntityTrackingDuration = duration;
        ForgeTimeTracker.tileEntityTracking = true;
        sender.sendMessage(new TextComponentTranslation("commands.forge.tracking.te.enabled", duration));
    }

    private void doTPSLog(MinecraftServer server, ICommandSender sender, String[] args)
    {

    }

    private void displayTPS(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        int dim = 0;
        boolean summary = true;
        if (args.length > 1)
        {
            dim = parseInt(args[1]);
            summary = false;
        }
        if (summary)
        {
            for (Integer dimId : DimensionManager.getIDs())
            {
                double worldTickTime = ForgeCommand.mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
                double worldTPS = Math.min(1000.0/worldTickTime, 20);
                sender.sendMessage(new TextComponentTranslation("commands.forge.tps.summary",String.format("Dim %d", dimId), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)));
            }
            double meanTickTime = ForgeCommand.mean(server.tickTimeArray) * 1.0E-6D;
            double meanTPS = Math.min(1000.0/meanTickTime, 20);
            sender.sendMessage(new TextComponentTranslation("commands.forge.tps.summary","Overall", timeFormatter.format(meanTickTime), timeFormatter.format(meanTPS)));
        }
        else
        {
            double worldTickTime = ForgeCommand.mean(server.worldTickTimes.get(dim)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sender.sendMessage(new TextComponentTranslation("commands.forge.tps.summary",String.format("Dim %d", dim), timeFormatter.format(worldTickTime), timeFormatter.format(worldTPS)));
        }
    }

    private static long mean(long[] values)
    {
        long sum = 0l;
        for (long v : values)
        {
            sum+=v;
        }

        return sum / values.length;
    }
}
