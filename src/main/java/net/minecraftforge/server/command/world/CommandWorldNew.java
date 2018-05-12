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

package net.minecraftforge.server.command.world;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

class CommandWorldNew extends AbstractWorldCommand
{

    CommandWorldNew(CommandWorldBase base)
    {
        super(base);
    }

    @Override
    public String getName()
    {
        return "new";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands.forge.loadworld.new.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(getUsage(sender));
        }
        String folderName = args[0];

        int delayS = 0;
        String seedString = null;
        WorldType type = WorldType.DEFAULT;
        String generatorOptions = "";
        switch (args.length)
        {
            default:
            case 5:
                generatorOptions = args[4];
            case 4:
                type = WorldType.parseWorldType(args[3]);
                if (type == null)
                {
                    throw new WrongUsageException("commands.forge.loadworld.new.wrong_world_type", args[3]);
                }
            case 3:
                seedString = args[2];
            case 2:
                delayS = Integer.valueOf(args[1]);
            case 1:
            case 0:
        }

        if (doesWorldExist(server, folderName))
        {
            throw new CommandException("commands.forge.loadworld.new.exists", folderName);
        }

        base.setJob(new NewWorldJob(server, sender, folderName, delayS, getSeedForString(seedString), type, generatorOptions));
    }

    /**
     * get a seed for given string
     * parses string as long if possible else gets strings hash code
     * in case of empty string returns a random seed
     * @param s the string to acquire the seed from
     * @return seed
     */
    public long getSeedForString(@Nullable String s) {
        long k = (new Random()).nextLong();

        if (s != null && !s.isEmpty())
        {
            try
            {
                long l = Long.parseLong(s);

                if (l != 0L)
                {
                    k = l;
                }
            }
            catch (NumberFormatException var16)
            {
                k = (long)s.hashCode();
            }
        }
        return k;
    }

    /* -----------------------------------------------------------------------------------------------------------------
        Tab Completion handling
     ---------------------------------------------------------------------------------------------------------------- */

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (server.isSinglePlayer())
        {
            return super.getTabCompletions(server, sender, args, targetPos);
        }
        if (args.length == 4)
        {
            List<String> typeNames = Arrays.stream(WorldType.WORLD_TYPES).filter(Objects::nonNull).map(WorldType::getName).collect(Collectors.toList());
            return getListOfStringsMatchingLastWord(args, typeNames);
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
