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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for commands that has subcommands.
 * <p>
 * E.g. /team settings set [value]
 * settings is subcommand of team and set is subcommand of settings
 */
public abstract class CommandTreeBase extends CommandBase
{
    private final Map<String, ICommand> commandMap = new HashMap<>();
    private final Map<String, ICommand> commandAliasMap = new HashMap<>();

    public void addSubcommand(ICommand command)
    {
        commandMap.put(command.getName(), command);
        for (String alias : command.getAliases())
        {
            commandAliasMap.put(alias, command);
        }
    }

    public Collection<ICommand> getSubCommands()
    {
        return commandMap.values();
    }

    @Nullable
    public ICommand getSubCommand(String command)
    {
        ICommand cmd = commandMap.get(command);
        if (cmd != null)
        {
            return cmd;
        }
        return commandAliasMap.get(command);
    }

    public Map<String, ICommand> getCommandMap()
    {
        return Collections.unmodifiableMap(commandMap);
    }

    public List<ICommand> getSortedCommandList()
    {
        List<ICommand> list = new ArrayList<>(getSubCommands());
        Collections.sort(list);
        return list;
    }

    private static String[] shiftArgs(@Nullable String[] s)
    {
        if(s == null || s.length == 0)
        {
            return new String[0];
        }

        String[] s1 = new String[s.length - 1];
        System.arraycopy(s, 1, s1, 0, s1.length);
        return s1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            List<String> keys = new ArrayList<>();

            for (ICommand c : getSubCommands())
            {
                if(c.checkPermission(server, sender))
                {
                    keys.add(c.getName());
                }
            }

            keys.sort(null);
            return getListOfStringsMatchingLastWord(args, keys);
        }

        ICommand cmd = getSubCommand(args[0]);

        if(cmd != null)
        {
            return cmd.getTabCompletions(server, sender, shiftArgs(args), pos);
        }

        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        if (index > 0 && args.length > 1)
        {
            ICommand cmd = getSubCommand(args[0]);
            if (cmd != null)
            {
                return cmd.isUsernameIndex(shiftArgs(args), index - 1);
            }
        }

        return false;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            String subCommandsString = getAvailableSubCommandsString(server, sender);
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.tree_base.available_subcommands", subCommandsString));
        }
        else
        {
            ICommand cmd = getSubCommand(args[0]);

            if(cmd == null)
            {
                String subCommandsString = getAvailableSubCommandsString(server, sender);
                throw new CommandException("commands.tree_base.invalid_cmd.list_subcommands", args[0], subCommandsString);
            }
            else if(!cmd.checkPermission(server, sender))
            {
                throw new CommandException("commands.generic.permission");
            }
            else
            {
                cmd.execute(server, sender, shiftArgs(args));
            }
        }
    }

    private String getAvailableSubCommandsString(MinecraftServer server, ICommandSender sender)
    {
        Collection<String> availableCommands = new ArrayList<>();
        for (ICommand command : getSubCommands())
        {
            if (command.checkPermission(server, sender))
            {
                availableCommands.add(command.getName());
            }
        }
        return CommandBase.joinNiceStringFromCollection(availableCommands);
    }
}
