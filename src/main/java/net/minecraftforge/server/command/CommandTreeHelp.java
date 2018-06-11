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

import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;

/**
 * Add help for parent and all its children.
 */
public class CommandTreeHelp extends CommandHelp implements ICommandWithParent
{
    private final CommandTreeBase parent;

    public CommandTreeHelp(CommandTreeBase parent)
    {
        this.parent = parent;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "commands." + getPath() + ".usage";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    protected List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server)
    {
        return parent.getAvailableSubCommands(server, sender);
    }

    @Override
    protected Map<String, ICommand> getCommandMap(MinecraftServer server)
    {
        return parent.getCommandMap();
    }

    @Override
    public ICommand getParent()
    {
        return parent;
    }
}