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

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.timings.ForgeTimings;
import net.minecraftforge.server.timings.TimeTracker;

class CommandTrack extends CommandTreeBase
{

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    public CommandTrack()
    {
        addSubcommand(new StartTrackingCommand());
        addSubcommand(new ResetTrackingCommand());
        addSubcommand(new TrackResultsTileEntity());
        addSubcommand(new TrackResultsEntity());
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
                TimeTracker.TILE_ENTITY_UPDATE.reset();
                TimeTracker.TILE_ENTITY_UPDATE.enable(duration);
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.te.enabled", duration));
            }
            else if ("entity".equals(type))
            {
                TimeTracker.ENTITY_UPDATE.reset();
                TimeTracker.ENTITY_UPDATE.enable(duration);
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
                TimeTracker.TILE_ENTITY_UPDATE.reset();
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.reset"));
            }
            else if ("entity".equals(type))
            {
                TimeTracker.ENTITY_UPDATE.reset();
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.reset"));
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
    }

    /**
     * A base command for all the tracking results commands
     *
     * @param <T>
     */
    private static abstract class TrackResultsBaseCommand<T> extends CommandBase
    {

        private TimeTracker<T> tracker;

        protected TrackResultsBaseCommand(TimeTracker<T> tracker)
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
            list.sort(Comparator.comparingDouble(ForgeTimings::getAverageTimings));
            Collections.reverse(list);

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
                timingsList.stream()
                        .filter(timings -> timings.getObject().get() != null)
                        .limit(10)
                        .forEach(timings -> sender.sendMessage(buildTrackString(sender, timings))
                        );
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

        /**
         * Translates a world dimension ID into a name
         *
         * @param dimId The dimension ID
         * @return The name of the dimension
         */
        protected String getWorldName(int dimId)
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

    private static class TrackResultsEntity extends TrackResultsBaseCommand<Entity>
    {
        public TrackResultsEntity()
        {
            super(TimeTracker.ENTITY_UPDATE);
        }

        @Override
        public String getName()
        {
            return "entity";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.forge.tracking.entity.usage";
        }

        @Override
        protected ITextComponent buildTrackString(ICommandSender sender, ForgeTimings<Entity> data)
        {
            Entity entity = data.getObject().get();
            if (entity == null)
                return TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.invalid");

            BlockPos currentPos = entity.getPosition();
            String world = getWorldName(entity.world.provider.getDimension());
            double averageTimings = data.getAverageTimings();
            String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000) : TIME_FORMAT.format(averageTimings)) + getTimeSuffix(
                    averageTimings);

            return TextComponentHelper.createComponentTranslation(sender, "commands.forge.tracking.timingEntry", entity.getName(),
                    world, currentPos.getX(), currentPos.getY(), currentPos.getZ(), tickTime);
        }
    }

    private static class TrackResultsTileEntity extends TrackResultsBaseCommand<TileEntity>
    {

        public TrackResultsTileEntity()
        {
            super(TimeTracker.TILE_ENTITY_UPDATE);
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
            ResourceLocation registryId = TileEntity.getKey(tileEntity.getClass());
            if (registryId == null)
                return tileEntity.getClass().getSimpleName();
            else
            {
                return registryId.toString();
            }
        }
    }
}
