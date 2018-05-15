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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Add help for parent and all its children.
 * Must be added to parent after all other commands.
 */
public class CommandTreeHelp extends CommandBase
{
    private final CommandTreeBase parent;

    public CommandTreeHelp(CommandTreeBase parent)
    {
        this.parent = parent;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getName()
    {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return parent.getUsage(sender) + ".help";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            List<String> keys = new ArrayList<>();

            for (ICommand c : parent.getSubCommands())
            {
                if(c.checkPermission(server, sender))
                {
                    keys.add(c.getName());
                }
            }

            keys.sort(null);
            return getListOfStringsMatchingLastWord(args, keys);
        }

        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, parent.getUsage(sender)));

            for (ICommand subCommand : parent.getSortedAvailableCommandList(server, sender))
            {
                if(subCommand != this)
                {
                    sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, subCommand.getUsage(sender)));
                }
            }
        }
        else
        {
            ICommand subCommand = parent.getSubCommand(args[0]);
            
            if(subCommand == null)
            {
                throw new CommandNotFoundException();
            }
            else if(!subCommand.checkPermission(server, sender))
            {
                throw new CommandException("commands.generic.permission");
            }
            else
            {
                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, subCommand.getUsage(sender)));    
            }
        }
    }
}
