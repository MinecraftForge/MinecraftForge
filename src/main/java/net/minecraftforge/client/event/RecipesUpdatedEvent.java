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

package net.minecraftforge.client.event;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when {@link RecipeManager} has all of its recipes updated.
 *
 * This happens on {@link Dist#CLIENT} when recipes are synced from the server to the client (just after a client has connected),
 * and on both {@link Dist#DEDICATED_SERVER} and {@link Dist#CLIENT} when datapacks are loaded or reloaded.
 */
public class RecipesUpdatedEvent extends Event
{
    public RecipesUpdatedEvent()
    {
    }
}
