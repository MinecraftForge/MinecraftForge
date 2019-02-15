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

import java.text.DecimalFormat;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

class CommandTps
{
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TextComponentTranslation("commands.forge.tps.invalid", dim, StreamSupport.stream(DimensionType.func_212681_b().spliterator(), false).map(d -> DimensionType.func_212678_a(d).toString()).sorted().collect(Collectors.joining(", "))));
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("tps")
            .requires(cs->cs.hasPermissionLevel(0)) //permission
            .then(Commands.argument("dim", DimensionArgument.func_212595_a())
                .executes(ctx -> sendTime(ctx.getSource(), DimensionArgument.func_212592_a(ctx, "dim")))
            )
            .executes(ctx -> {
                for (DimensionType dim : DimensionType.func_212681_b())
                    sendTime(ctx.getSource(), dim);

                double meanTickTime = mean(ctx.getSource().getServer().tickTimeArray) * 1.0E-6D;
                double meanTPS = Math.min(1000.0/meanTickTime, 20);
                ctx.getSource().sendFeedback(new TextComponentTranslation("commands.forge.tps.summary.all", TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS)), true);

                return 0;
            }
        );
    }

    private static int sendTime(CommandSource cs, DimensionType dim) throws CommandSyntaxException
    {
        long[] times = cs.getServer().getTickTime(dim);

        if (times == null)
            throw INVALID_DIMENSION.create(DimensionType.func_212678_a(dim));

        double worldTickTime = mean(times) * 1.0E-6D;
        double worldTPS = Math.min(1000.0 / worldTickTime, 20);
        cs.sendFeedback(new TextComponentTranslation("commands.forge.tps.summary.named", dim.getId(), DimensionType.func_212678_a(dim), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)), true);

        return 1;
    }

    private static long mean(long[] values)
    {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }
}
