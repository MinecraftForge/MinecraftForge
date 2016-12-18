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

package net.minecraftforge.common.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;

/**
 * Handler for custom types of selectors registered with {@link SelectorHandlerManager}
 */
public abstract class SelectorHandler
{
    /**
     * Returns a {@link List} of {@link Entity Entities} of class {@code targetClass} ({@code T}) represented by {@code token}
     * 
     * @param sender The {@link ICommandSender} that initiated the query
     */
    public abstract <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass) throws CommandException;

    /**
     * Returns whether the selector string potentially matches multiple entities
     */
    public abstract boolean matchesMultiplePlayers(String selectorStr) throws CommandException;

    /**
     * Returns whether the string represents a selector <br>
     * This method indicated whether {@link #matchEntities this.matchEntities} should be used to resolve the string<br>
     * <b>Note:</b> Since the handler is chosen based on the registered prefix, this will mostly return {@code true}<br>
     * <b>Note:</b> Mostly for legacy reasons.
     * (Since {@link net.minecraft.command.EntitySelector#matchEntities(ICommandSender, String, Class) EntitySelector.matchEntities}
     * returns an empty list when the string is illformed instead of throwing an exception (as is indirectly done when this method returns {@code false})
     */
    public boolean isSelector(String selectorStr)
    {
        return true;
    }
}
