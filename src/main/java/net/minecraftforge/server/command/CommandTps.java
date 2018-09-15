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
import java.util.stream.Collectors;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

class CommandTps
{
    private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TextComponentTranslation("commands.forge.tps.invalid", dim, DimensionManager.getIDStream().sorted().map(id -> id.toString()).collect(Collectors.joining(", "))));
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.func_197057_a("tps")
            .requires(cs->cs.func_197034_c(0)) //permission
            .then(Commands.func_197056_a("dim", IntegerArgumentType.integer())
                .suggests((ctx, builder) -> ISuggestionProvider.func_197013_a(DimensionManager.getIDStream().sorted().map(id -> id.toString()), builder))
                .executes(ctx -> sendTime(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "dim")))
            )
            .executes(ctx -> {
                for (Integer dim : (Iterable<Integer>)DimensionManager.getIDStream().sorted()::iterator)
                    sendTime(ctx.getSource(), dim);

                double meanTickTime = mean(ctx.getSource().func_197028_i().tickTimeArray) * 1.0E-6D;
                double meanTPS = Math.min(1000.0/meanTickTime, 20);
                ctx.getSource().func_197030_a(new TextComponentTranslation("commands.forge.tps.summary.all", TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS)), true);

                return 0;
            }
        );
    }

    private static long[] getTimes(int dim)
    {
        //TODO: Implement, needs patch.
        return  null;
    }

    private static int sendTime(CommandSource cs, int dim) throws CommandSyntaxException
    {
        long[] times = getTimes(dim);

        if (times == null)
            throw INVALID_DIMENSION.create(dim);

        double worldTickTime = mean(times) * 1.0E-6D;
        double worldTPS = Math.min(1000.0 / worldTickTime, 20);
        DimensionType type = DimensionManager.getProviderType(dim);
        if (type == null)
            cs.func_197030_a(new TextComponentTranslation("commands.forge.tps.summary.basic", dim, TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)), true);
        else
            cs.func_197030_a(new TextComponentTranslation("commands.forge.tps.summary.named", dim, type.getName(), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)), true);

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
