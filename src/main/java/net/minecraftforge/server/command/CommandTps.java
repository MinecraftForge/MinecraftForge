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

import java.text.DecimalFormat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

class CommandTps extends CommandBase
{
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

    @Override
    public String getName()
    {
        return "tps";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.tps.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        int dim = 0;
        boolean summary = true;
        if (args.length > 0)
        {
            dim = parseInt(args[0]);
            summary = false;
        }

        if (summary)
        {
            for (Integer dimId : DimensionManager.getIDs())
            {
                double worldTickTime = mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
                double worldTPS = Math.min(1000.0/worldTickTime, 20);
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tps.summary", getDimensionPrefix(dimId), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)));
            }
            double meanTickTime = mean(server.tickTimeArray) * 1.0E-6D;
            double meanTPS = Math.min(1000.0/meanTickTime, 20);
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tps.summary","Overall", TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS)));
        }
        else
        {
            double worldTickTime = mean(server.worldTickTimes.get(dim)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tps.summary", getDimensionPrefix(dim), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)));
        }
    }

    private static String getDimensionPrefix(int dimId)
    {
        DimensionType providerType = DimensionManager.getProviderType(dimId);
        if (providerType == null)
        {
            return String.format("Dim %2d", dimId);
        }
        else
        {
            return String.format("Dim %2d (%s)", dimId, providerType.getName());
        }
    }

    private static long mean(long[] values)
    {
        long sum = 0L;
        for (long v : values)
        {
            sum += v;
        }
        return sum / values.length;
    }
}
