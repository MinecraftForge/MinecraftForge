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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.ForgeTimeTracker;
import net.minecraftforge.server.timings.ForgeTimings;

class CommandTrack extends CommandTreeBase
{

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    public CommandTrack()
    {
        addSubcommand(new StartTrackingCommand());
        addSubcommand(new ResetTrackingCommand());
        addSubcommand(new TrackResultsTileEntity());
        addSubcommand(new CommandTreeHelp(this));
    }

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
        return "commands.forge.tracking.usage";
    }

    private static class StartTrackingCommand extends CommandBase
    {

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length != 2)
            {
                throw new WrongUsageException(getUsage(sender));
            }
            String type = args[0];
            int duration = parseInt(args[1], 1, 60);
            if ("te".equals(type))
            {
                ForgeTimeTracker.TILE_ENTITY_UPDATE.reset();
                ForgeTimeTracker.TILE_ENTITY_UPDATE.trackingDuration = duration;
                ForgeTimeTracker.TILE_ENTITY_UPDATE.enabled = true;
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.te.enabled", duration));
            }
            else if ("entity".equals(type))
            {
                ForgeTimeTracker.ENTITY_UPDATE.reset();
                ForgeTimeTracker.ENTITY_UPDATE.trackingDuration = duration;
                ForgeTimeTracker.ENTITY_UPDATE.enabled = true;
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.entity.enabled", duration));
            }
            else
            {
                throw new WrongUsageException(getUsage(sender));
            }
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
        {
            return Arrays.asList("te", "entity");
        }

        @Override
        public String getName()
        {
            return "start";
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 2;
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.forge.tracking.start.usage";
        }
    }

    private static class ResetTrackingCommand extends CommandBase
    {
        @Override
        public String getName()
        {
            return "reset";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.forge.tracking.reset.usage";
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 2;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length != 1)
            {
                throw new WrongUsageException(getUsage(sender));
            }
            String type = args[0];
            if ("te".equals(type))
            {
                ForgeTimeTracker.TILE_ENTITY_UPDATE.reset();
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.reset"));
            }
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
        {
            return Arrays.asList("te", "entity");
        }
    }

    /**
     * A base command for all the tracking results commands
     *
     * @param <T>
     */
    private static abstract class TrackResultsBaseCommand<T> extends CommandBase
    {

        private ForgeTimeTracker<T> tracker;

        protected TrackResultsBaseCommand(ForgeTimeTracker<T> tracker)
        {
            this.tracker = tracker;
        }

        /**
         * Returns the time objects recorded by the time tracker sorted by average time
         *
         * @return A list of time objects
         */
        protected List<ForgeTimings<T>> getSortedTimings()
        {
            ArrayList<ForgeTimings<T>> list = new ArrayList<>();
            list.addAll(tracker.getTimingData());
            list.sort((o1, o2) -> Double.compare(o2.getAverageTimings(), o1.getAverageTimings()));
            return list;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            List<ForgeTimings<T>> timingsList = getSortedTimings();
            if (timingsList.isEmpty())
            {
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.noData"));
            }
            else
            {
                List<ForgeTimings<T>> subList = new ArrayList<>();
                int count = 0;
                for (ForgeTimings<T> e : timingsList)
                {
                    T te = e.getObject().get();
                    if (te == null)
                        continue;
                    subList.add(e);
                    if (++count > 10)
                        break;
                }
                for (ForgeTimings<T> e : subList)
                {
                    T te = e.getObject().get();

                    if (te == null)
                        continue; // Probably shouldn't ever happen

                    sender.sendMessage(buildTrackString(sender, e));
                }
            }
        }

        protected abstract ITextComponent buildTrackString(ICommandSender sender, ForgeTimings<T> data);

        /**
         * Gets the time suffix for the provided time in nanoseconds
         *
         * @param time The time in nanoseconds
         * @return The time suffix
         */
        protected String getTimeSuffix(double time)
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
    }

    private static class TrackResultsTileEntity extends TrackResultsBaseCommand<TileEntity>
    {

        public TrackResultsTileEntity()
        {
            super(ForgeTimeTracker.TILE_ENTITY_UPDATE);
        }

        @Override
        public String getName()
        {
            return "te";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.forge.tracking.te.usage";
        }

        @Override
        protected ITextComponent buildTrackString(ICommandSender sender, ForgeTimings<TileEntity> data)
        {
            TileEntity te = data.getObject().get();
            if (te == null)
                return TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.invalid");

            String name = getTileEntityName(te);
            BlockPos pos = te.getPos();

            double averageTimings = data.getAverageTimings();
            String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000) : TIME_FORMAT.format(averageTimings)) + getTimeSuffix(
                    averageTimings);
            return TextComponentHelper
                    .createComponentTranslation(sender, "commands.forge.tracking.timingEntry", name,
                            getWorldName(te.getWorld().provider.getDimension()),
                            pos.getX(), pos.getY(), pos.getZ(), tickTime);
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
    }
}
