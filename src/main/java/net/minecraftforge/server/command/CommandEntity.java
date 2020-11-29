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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

class CommandEntity
{
    static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands.literal("entity")
                .then(EntityListCommand.register()); //TODO: //Kill, spawn, etc..
    }

    private static class EntityListCommand
    {
        private static final SimpleCommandExceptionType INVALID_FILTER = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.entity.list.invalid"));
        private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType(dim -> new TranslationTextComponent("commands.forge.entity.list.invalidworld", dim));
        private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.entity.list.none"));
        static ArgumentBuilder<CommandSource, ?> register()
        {
            return Commands.literal("list")
                .requires(cs->cs.hasPermissionLevel(2)) //permission
                .then(Commands.argument("filter", StringArgumentType.string())
                    .suggests((ctx, builder) -> ISuggestionProvider.suggest(ForgeRegistries.ENTITIES.getKeys().stream().map(ResourceLocation::toString).map(StringArgumentType::escapeIfRequired), builder))
                    .then(Commands.argument("dim", DimensionArgument.getDimension())
                        .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "filter"), DimensionArgument.getDimensionArgument(ctx, "dim").getDimensionKey()))
                    )
                    .executes(ctx -> execute(ctx.getSource(), StringArgumentType.getString(ctx, "filter"), ctx.getSource().getWorld().getDimensionKey()))
                )
                .executes(ctx -> execute(ctx.getSource(), "*", ctx.getSource().getWorld().getDimensionKey()));
        }

        private static int execute(CommandSource sender, String filter, RegistryKey<World> dim) throws CommandSyntaxException
        {
            final String cleanFilter = filter.replace("?", ".?").replace("*", ".*?");

            Set<ResourceLocation> names = ForgeRegistries.ENTITIES.getKeys().stream().filter(n -> n.toString().matches(cleanFilter)).collect(Collectors.toSet());

            if (names.isEmpty())
                throw INVALID_FILTER.create();

            ServerWorld world = sender.getServer().getWorld(dim); //TODO: DimensionManager so we can hotload? DimensionManager.getWorld(sender.getServer(), dim, false, false);
            if (world == null)
                throw INVALID_DIMENSION.create(dim);

            Map<ResourceLocation, MutablePair<Integer, Map<ChunkPos, Integer>>> list = Maps.newHashMap();
            world.getEntities().forEach(e -> {
                MutablePair<Integer, Map<ChunkPos, Integer>> info = list.computeIfAbsent(e.getType().getRegistryName(), k -> MutablePair.of(0, Maps.newHashMap()));
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

                sender.sendFeedback(new TranslationTextComponent("commands.forge.entity.list.single.header", name, info.getLeft()), false);
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
                    sender.sendFeedback(new StringTextComponent("  " + e.getValue() + ": " + e.getKey().x + ", " + e.getKey().z), false);
                }
                return toSort.size();
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
                sender.sendFeedback(new TranslationTextComponent("commands.forge.entity.list.multiple.header", count), false);
                info.forEach(e -> sender.sendFeedback(new StringTextComponent("  " + e.getValue() + ": " + e.getKey()), false));
                return info.size();
            }
        }
    }

}
