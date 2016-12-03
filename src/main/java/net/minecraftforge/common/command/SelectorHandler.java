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

import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * A proxy for {@link EntitySelector} methods. Can be used to customize the parsing behavior<br>
 * <b>Note:</b>  You should generally override all three methods to produce consistent results<br>
 * <b>Note:</b> These methods are called on both client and server<br>
 * <b>Note:</b>  For compatibility reasons, you should not break the key=value pattern
 */
public class SelectorHandler
{
    public <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass) throws CommandException
    {
        return EntitySelector.matchEntitiesDefault(sender, token, targetClass);
    }

    /**
     * This should raise an {@link net.minecraftforge.event.EntitySelectorEvent EntitySelectorEvent}
     */
    public boolean matchesMultiplePlayers(String selectorStr) throws CommandException
    {
        return EntitySelector.matchesMultiplePlayersDefault(selectorStr);
    }

    public boolean hasArguments(String selectorStr)
    {
        return EntitySelector.hasArgumentsDefault(selectorStr);
    }
}
