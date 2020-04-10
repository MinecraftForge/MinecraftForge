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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

class CommandEntity extends CommandTreeBase
{
    public CommandEntity()
    {
        addSubcommand(new EntityListCommand());
        addSubcommand(new CommandTreeHelp(this));
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.entity.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "entity";
    }

    private static class EntityListCommand extends CommandBase
    {
        @Override
        public String getName()
        {
            return "list";
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 2;
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "commands.forge.entity.list.usage";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            String filter = "*";
            if (args.length > 0)
            {
                filter = args[0];
            }
            final String cleanFilter = filter.replace("?", ".?").replace("*", ".*?");
            Set<ResourceLocation> names = EntityList.getEntityNameList().stream().filter(n -> n.toString().matches(cleanFilter)).collect(Collectors.toSet());

            if (names.isEmpty())
                throw new WrongUsageException("commands.forge.entity.list.invalid");

            int dim = args.length > 1 ? parseInt(args[1]) : sender.getEntityWorld().provider.getDimension();

            WorldServer world = DimensionManager.getWorld(dim);
            if (world == null)
                throw new WrongUsageException("commands.forge.entity.list.invalidworld", dim);

            Map<ResourceLocation, MutablePair<Integer, Map<ChunkPos, Integer>>> list = Maps.newHashMap();
            List<Entity> entities = world.loadedEntityList;
            entities.forEach(e -> {
                ResourceLocation key = EntityList.getKey(e);

                MutablePair<Integer, Map<ChunkPos, Integer>> info = list.computeIfAbsent(key, k -> MutablePair.of(0, Maps.newHashMap()));
                ChunkPos chunk = new ChunkPos(e.getPosition());
                info.left++;
                info.right.put(chunk, info.right.getOrDefault(chunk, 0) + 1);
            });

            if (names.size() == 1)
            {
                ResourceLocation name = names.iterator().next();
                Pair<Integer, Map<ChunkPos, Integer>> info = list.get(name);
                if (info == null)
                    throw new WrongUsageException("commands.forge.entity.list.none");
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.entity.list.single.header", name, info.getLeft()));
                List<Map.Entry<ChunkPos, Integer>> toSort = new ArrayList<>();
                toSort.addAll(info.getRight().entrySet());
                toSort.sort((a, b) -> {
                    if (Objects.equals(a.getValue(), b.getValue()))
                    {
                        return a.getKey().toString().compareTo(b.getKey().toString());
                    }
                    else
                    {
                        return b.getValue() - a.getValue();
                    }
                });
                long limit = 10;
                for (Map.Entry<ChunkPos, Integer> e : toSort)
                {
                    if (limit-- == 0) break;
                    sender.sendMessage(new TextComponentString("  " + e.getValue() + ": " + e.getKey().x + ", " + e.getKey().z));
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
                    {
                        return a.getKey().toString().compareTo(b.getKey().toString());
                    }
                    else
                    {
                        return b.getRight() - a.getRight();
                    }
                });

                if (info.size() == 0)
                    throw new WrongUsageException("commands.forge.entity.list.none");

                int count = info.stream().mapToInt(Pair::getRight).sum();
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.entity.list.multiple.header", count));
                info.forEach(e -> sender.sendMessage(new TextComponentString("  " + e.getValue() + ": " + e.getKey())));
            }
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
        {
            if (args.length == 1)
            {
                String[] entityNames = EntityList.getEntityNameList().stream().map(ResourceLocation::toString).sorted().toArray(String[]::new);
                return getListOfStringsMatchingLastWord(args, entityNames);
            }
            return Collections.emptyList();
        }
    }
}
