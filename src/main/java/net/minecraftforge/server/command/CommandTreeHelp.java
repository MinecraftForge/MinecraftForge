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

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Add help for parent and all its children.
 * Must be added to parent after all other commands.
 */
public class CommandTreeHelp extends CommandTreeBase
{
    private final ICommand parent;

    public CommandTreeHelp(CommandTreeBase parent)
    {
        this.parent = parent;
        for (ICommand command : parent.getSubCommands())
        {
            addSubcommand(new HelpSubCommand(this, command));
        }
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
        return "commands.forge.usage.help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, parent.getUsage(sender)));
            for (ICommand subCommand : getSubCommands())
            {
                if (subCommand instanceof HelpSubCommand && subCommand.checkPermission(server, sender))
                {
                    subCommand.execute(server, sender, args);
                }
            }
            return;
        }
        super.execute(server, sender, args);
    }

    public static class HelpSubCommand extends CommandBase
    {
        private final CommandTreeHelp parent;
        private final ICommand command;

        public HelpSubCommand(CommandTreeHelp parent, ICommand command)
        {
            this.parent = parent;
            this.command = command;
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public String getName()
        {
            return command.getName();
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return command.getUsage(sender);
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender)
        {
            return command.checkPermission(server, sender);
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, command.getUsage(sender)));
        }
    }
}
