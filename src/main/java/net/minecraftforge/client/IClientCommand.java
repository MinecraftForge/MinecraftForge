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

package net.minecraftforge.client;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

/**
 * Client-side commands can implement this interface to allow additional control over when the command may be used.
 */
public interface IClientCommand extends ICommand
{

    /**
     * Determine whether this command can be used without the "/" prefix. By default this is true.
     * @param sender the command sender
     * @param message the message, without potential prefix
     * @return true to allow the usage of this command without the prefix
     */
    boolean allowUsageWithoutPrefix(ICommandSender sender, String message);

}
