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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

class CommandEntity
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.func_197057_a("entity")
                .then(EntityListCommand.register()); //TODO: //Kill, spawn, etc..
    }

    private static class EntityListCommand
    {
        private static final SimpleCommandExceptionType INVALID_FILTER = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forge.entity.list.invalid"));
        private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TextComponentTranslation("commands.forge.entity.list.invalidworld", dim));
        private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TextComponentTranslation("commands.forge.entity.list.none"));
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.func_197057_a("list")
                .requires(cs->cs.func_197034_c(2)) //permission
                .then(Commands.func_197056_a("filter", StringArgumentType.string())
                    .suggests((ctx, builder) -> ISuggestionProvider.func_197013_a(ForgeRegistries.ENTITIES.getKeys().stream().map(id -> id.toString()), builder))
                    .then(Commands.func_197056_a("dim", IntegerArgumentType.integer())
                        .suggests((ctx, builder) -> ISuggestionProvider.func_197013_a(DimensionManager.getIDStream().sorted().map(id -> id.toString()), builder))
                        .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "filter"), IntegerArgumentType.getInteger(ctx, "dim")))
                    )
                    .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "filter"), ctx.getSource().func_197023_e().provider.getDimension()))
                )
                .executes(ctx -> execute(ctx.getSource(), "*", ctx.getSource().func_197023_e().provider.getDimension()));
        }

        private static int execute(CommandSource sender, String filter, int dim) throws CommandException
        {
            final String cleanFilter = filter.replace("?", ".?").replace("*", ".*?");

            Set<ResourceLocation> names = ForgeRegistries.ENTITIES.getKeys().stream().filter(n -> n.toString().matches(cleanFilter)).collect(Collectors.toSet());

            if (names.isEmpty())
                throw INVALID_FILTER.create();

            WorldServer world = DimensionManager.getWorld(dim);
            if (world == null)
                throw INVALID_DIMENSION.create(dim);

            Map<ResourceLocation, MutablePair<Integer, Map<ChunkPos, Integer>>> list = Maps.newHashMap();
            List<Entity> entities = world.loadedEntityList;
            entities.forEach(e -> {
                MutablePair<Integer, Map<ChunkPos, Integer>> info = list.computeIfAbsent(e.func_200600_R().getRegistryName(), k -> MutablePair.of(0, Maps.newHashMap()));
                ChunkPos chunk = new ChunkPos(e.getPosition());
                info.left++;
                info.right.put(chunk, info.right.getOrDefault(chunk, 0) + 1);
            });

            if (names.size() == 1)
            {
                ResourceLocation name = names.iterator().next();
                Pair<Integer, Map<ChunkPos, Integer>> info = list.get(name);
                if (info == null)
                    throw NO_ENTITIES.create();

                sender.func_197030_a(new TextComponentTranslation("commands.forge.entity.list.single.header", name, info.getLeft()), true);
                List<Map.Entry<ChunkPos, Integer>> toSort = new ArrayList<>();
                toSort.addAll(info.getRight().entrySet());
                toSort.sort((a, b) -> {
                    if (Objects.equals(a.getValue(), b.getValue()))
                        return a.getKey().toString().compareTo(b.getKey().toString());
                    else
                        return b.getValue() - a.getValue();
                });

                long limit = 10;
                for (Map.Entry<ChunkPos, Integer> e : toSort)
                {
                    if (limit-- == 0) break;
                    sender.func_197030_a(new TextComponentString("  " + e.getValue() + ": " + e.getKey().x + ", " + e.getKey().z), true);
                }
            }
            else
            {

                List<Pair<ResourceLocation, Integer>> info = new ArrayList<>();
                list.forEach((key, value) -> {
                    if (names.contains(key))
                    {
                        Pair<ResourceLocation, Integer> of = Pair.of(key, value.left);
                        info.add(of);
                    }
                });
                info.sort((a, b) -> {
                    if (Objects.equals(a.getRight(), b.getRight()))
                        return a.getKey().toString().compareTo(b.getKey().toString());
                    else
                        return b.getRight() - a.getRight();
                });

                if (info.size() == 0)
                    throw NO_ENTITIES.create();

                int count = info.stream().mapToInt(Pair::getRight).sum();
                sender.func_197030_a(new TextComponentTranslation("commands.forge.entity.list.multiple.header", count), true);
                info.forEach(e -> sender.func_197030_a(new TextComponentString("  " + e.getValue() + ": " + e.getKey()), true));
            }
        }
    }

}
