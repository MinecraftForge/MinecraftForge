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

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import javax.annotation.Nullable;

/**
 * Should be implemented by all commands that are part of command tree.
 * Default implementation of this class is CommandNode.
 */
public interface ICommandWithParent extends ICommand
{
	@Nullable
    ICommand getParent();

    @Override
    default String getUsage(ICommandSender sender)
    {
        return "commands." + getPath() + ".usage";
    }

    default String getPath()
    {
        if (getParent() instanceof ICommandWithParent)
        {
            return ((ICommandWithParent) getParent()).getPath() + '.' + getName();
        }

        return getName();
    }
}