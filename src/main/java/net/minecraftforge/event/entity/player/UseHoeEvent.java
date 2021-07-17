/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event.entity.player;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

import javax.annotation.Nonnull;

/**
 * This event is fired when a player attempts to use a Hoe on a block, it
 * can be canceled to completely prevent any further processing.
 *
 * You can also set the result to ALLOW to mark the event as processed
 * and damage the hoe.
 *
 * setResult(ALLOW) is the same as the old setHandled();
 * 
 * TODO: 1.17 Remove
 */
@Cancelable
@HasResult
@Deprecated
public class UseHoeEvent extends PlayerEvent
{
    private final UseOnContext context;;

    public UseHoeEvent(UseOnContext context)
    {
        super(context.getPlayer());
        this.context = context;
    }

    @Nonnull
    public UseOnContext getContext()
    {
        return context;
    }
}
