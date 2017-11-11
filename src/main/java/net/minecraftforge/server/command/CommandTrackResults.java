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
import java.util.*;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.ForgeTimeTracker;
import net.minecraftforge.server.timings.ForgeTimings;

public class CommandTrackResults extends CommandBase
{

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    @Override
    public String getName()
    {
        return "track-results";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.usage.trackresults";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.forge.usage.trackresults");
        }
        String type = args[0];
        if ("te".equals(type))
        {
            // Display the top 10 slowest TEs and their tick times
            List<ForgeTimings<TileEntity>> timingList = getSortedTileEntityTimings();
            if (timingList.isEmpty())
            {
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.timings.nodata"));
            }
            else
            {
                for (ForgeTimings<TileEntity> timings : timingList.subList(0, Math.min(10, timingList.size())))
                {
                    TileEntity t = timings.getObject().get();
                    if (t == null || t.isInvalid())
                        continue; // Object has ben GC'd or otherwise removed
                    String className = getTileEntityName(t);
                    BlockPos pos = t.getPos();

                    double averageTimings = timings.getAverageTimings();
                    String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000) : TIME_FORMAT.format(averageTimings)) + getTimeSuffix(
                            averageTimings);
                    sender.sendMessage(TextComponentHelper
                            .createComponentTranslation(sender, "commands.forge.timings.timingEntry", className,
                                    getWorldName(t.getWorld().provider.getDimension()), pos.getX(), pos.getY(), pos.getZ(),
                                    tickTime));
                }
            }

        }
        else
        {
            throw new WrongUsageException("commands.forge.usage.trackresults");
        }
    }

    private String getTimeSuffix(double time)
    {
        if (time < 1000)
        {
            return "µs";
        }
        else
        {
            return "ms";
        }
    }

    private String getTileEntityName(TileEntity tileEntity)
    {
        String className = tileEntity.getClass().getSimpleName();

        ResourceLocation registryId = TileEntity.getKey(tileEntity.getClass());
        if (registryId == null)
            return className;
        else
        {
            return registryId.toString();
        }
    }

    private String getWorldName(int dimId)
    {
        DimensionType type = DimensionManager.getProviderType(dimId);
        if (type == null)
        {
            return "Dim " + dimId;
        }
        else
        {
            return type.getName();
        }
    }

    private List<ForgeTimings<TileEntity>> getSortedTileEntityTimings()
    {
        ArrayList<ForgeTimings<TileEntity>> list = new ArrayList<>();
        list.addAll(ForgeTimeTracker.TILE_ENTITY_UPDATE.getTimingData());
        list.sort((o1, o2) -> Double.compare(o2.getAverageTimings(), o1.getAverageTimings()));
        return list;
    }

    private Map<ChunkPos, Double> getChunkTickTimes()
    {
        HashMap<ChunkPos, Double> times = new HashMap<>();
        HashMap<ChunkPos, Integer> count = new HashMap<>();

        ForgeTimeTracker.TILE_ENTITY_UPDATE.getTimingData().forEach(timings -> {
            TileEntity te = timings.getObject().get();
            if (te == null || te.isInvalid())
                return;
            ChunkPos pos = new ChunkPos(te.getPos());
            double t = times.getOrDefault(pos, 0.0);
            int c = count.getOrDefault(pos, 0);
            for (int microSeconds : timings.getRawTimingData())
            {
                t += microSeconds;
                c++;
            }
            times.put(pos, t);
            count.put(pos, c);
        });

        for (Map.Entry<ChunkPos, Double> entries : times.entrySet())
        {
            entries.setValue(entries.getValue() / count.getOrDefault(entries.getKey(), 1));
        }
        return times;
    }
}
