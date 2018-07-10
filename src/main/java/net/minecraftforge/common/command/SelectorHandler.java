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

package net.minecraftforge.common.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;

/**
 * Handler for custom types of selectors registered with {@link SelectorHandlerManager}
 */
public interface SelectorHandler
{
    /**
     * Returns a {@link List} of {@link Entity Entities} of class {@code targetClass} ({@code T}) represented by {@code token}<br>
     * <b>Note:</b> If {@code token} does not match the overall syntax defined by {@link #isSelector}, this method should return an empty list.
     * For any other error, an exception should be thrown
     * 
     * @param sender The {@link ICommandSender} that initiated the query
     */
    <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass) throws CommandException;

    /**
     * Returns whether the selector string potentially matches multiple entities
     */
    boolean matchesMultiplePlayers(String selectorStr) throws CommandException;

    /**
     * Returns whether the string matches the overall syntax of the selector<br>
     * <b>Note:</b> If this returns {@code false}, {@link #matchEntities} should return an empty list
     */
    boolean isSelector(String selectorStr);
}
