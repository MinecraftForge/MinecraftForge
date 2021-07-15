/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.server.timings.ForgeRegistryObjectHolder;
import net.minecraftforge.server.timings.ForgeTimings;
import net.minecraftforge.server.timings.TimeTracker;

class CommandTrack
{
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("track")
            .then(StartTrackingCommand.register())
            .then(ResetTrackingCommand.register())
            .then(TrackResultsEntity.register())
            .then(TrackResultsTileEntity.register())
            .then(TrackResultsBlockFluid.register())
            .then(StartTrackingCommand.register());
    }

    private static class StartTrackingCommand
    {
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.literal("start")
                .requires(cs->cs.hasPermission(2)) //permission
                .then(genCmd("te", TimeTracker.TILE_ENTITY_UPDATE))
                .then(genCmd("entity", TimeTracker.ENTITY_UPDATE))
                .then(genCmd("block_fluid", TimeTracker.BLOCK_FLUID_UPDATE));
        }

        static ArgumentBuilder<CommandSource, ?> genCmd(String descriptor, TimeTracker<?> tracker) {
            return Commands.literal(descriptor)
                    .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            int duration = IntegerArgumentType.getInteger(ctx, "duration");
                            tracker.reset();
                            tracker.enable(duration);
                            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.forge.tracking." + descriptor + ".enabled", duration), true);
                            return 0;
                        }));
        }
    }

    private static class ResetTrackingCommand
    {
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.literal("reset")
                .requires(cs->cs.hasPermission(2)) //permission
                .then(genCmd("te", TimeTracker.TILE_ENTITY_UPDATE))
                .then(genCmd("entity", TimeTracker.ENTITY_UPDATE))
                .then(genCmd("block_fluid", TimeTracker.BLOCK_FLUID_UPDATE));
        }

        static ArgumentBuilder<CommandSource, ?> genCmd(String descriptor, TimeTracker<?> tracker) {
            return Commands.literal(descriptor)
                    .executes(ctx -> {
                        tracker.reset();
                        ctx.getSource().sendSuccess(new TranslationTextComponent("commands.forge.tracking." + descriptor + ".reset"), true);
                        return 0;
                    });
        }
    }

    private static class TrackResults
    {
        /**
         * Returns the time objects recorded by the time tracker sorted by average time
         *
         * @return A list of time objects
         */
        private static <T> List<ForgeTimings<T>> getSortedTimings(TimeTracker<T> tracker)
        {
            ArrayList<ForgeTimings<T>> list = new ArrayList<>();

            list.addAll(tracker.getTimingData());
            list.sort(Comparator.comparingDouble(ForgeTimings::getAverageTimings));
            Collections.reverse(list);

            return list;
        }

        private static <T> int execute(CommandSource source, TimeTracker<T> tracker, Function<ForgeTimings<T>, ITextComponent> toString) throws CommandException
        {
            List<ForgeTimings<T>> timingsList = getSortedTimings(tracker);
            if (timingsList.isEmpty())
            {
                source.sendSuccess(new TranslationTextComponent("commands.forge.tracking.no_data"), true);
            }
            else
            {
                timingsList.stream()
                        .filter(timings -> timings.getObject().get() != null)
                        .limit(10)
                        .forEach(timings -> source.sendSuccess(toString.apply(timings), true));
            }
            return 0;
        }
    }

    private static class TrackResultsEntity
    {
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.literal("entity").executes(ctx -> TrackResults.execute(ctx.getSource(), TimeTracker.ENTITY_UPDATE, data ->
                {
                    Entity entity = data.getObject().get();
                    if (entity == null)
                        return new TranslationTextComponent("commands.forge.tracking.invalid");

                    BlockPos pos = entity.blockPosition();
                    double averageTimings = data.getAverageTimings();
                    String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000) : TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000 ? "\u03bcs" : "ms");

                    return new TranslationTextComponent("commands.forge.tracking.timing_entry", entity.getType().getRegistryName(), entity.level.dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ(), tickTime);
                })
            );
        }
    }

    private static class TrackResultsTileEntity
    {
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.literal("te").executes(ctx -> TrackResults.execute(ctx.getSource(), TimeTracker.TILE_ENTITY_UPDATE, data ->
                {
                    TileEntity te = data.getObject().get();
                    if (te == null)
                        return new TranslationTextComponent("commands.forge.tracking.invalid");

                    BlockPos pos = te.getBlockPos();

                    double averageTimings = data.getAverageTimings();
                    String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000) : TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000 ? "\u03bcs" : "ms");
                    return new TranslationTextComponent("commands.forge.tracking.timing_entry", te.getType().getRegistryName(), te.getLevel().dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ(), tickTime);
                })
            );
        }
    }

    private static class TrackResultsBlockFluid
    {
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.literal("block_fluid").executes(ctx -> TrackResults.execute(ctx.getSource(), TimeTracker.BLOCK_FLUID_UPDATE, data ->
                    {
                        ForgeRegistryObjectHolder<?> registryObject = data.getObject().get();
                        ResourceLocation registryEntry = null;
                        if (registryObject.object instanceof BlockState) {
                            registryEntry = ((BlockState) registryObject.object).getBlock().getRegistryName();
                        } else if (registryObject.object instanceof FluidState) {
                            registryEntry = ((FluidState) registryObject.object).getType().getRegistryName();
                        }
                        if (registryObject == null || registryEntry == null)
                            return new TranslationTextComponent("commands.forge.tracking.invalid");

                        BlockPos pos = registryObject.pos;

                        double averageTimings = data.getAverageTimings();
                        String tickTime = (averageTimings > 1000 ? TIME_FORMAT.format(averageTimings / 1000) : TIME_FORMAT.format(averageTimings)) + (averageTimings < 1000 ? "\u03bcs" : "ms");
                        return new TranslationTextComponent("commands.forge.tracking.timing_entry",
                                registryEntry, registryObject.dimension.location().toString(), pos.getX(), pos.getY(), pos.getZ(), tickTime);
                    })
            );
        }
    }
}
