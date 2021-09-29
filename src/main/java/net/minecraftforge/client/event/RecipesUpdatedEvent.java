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

package net.minecraftforge.client.event;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired on {@link Dist#CLIENT} when {@link RecipeManager} has all of its recipes synced from the server to the client (just after a client has connected),
 */
public class RecipesUpdatedEvent extends Event
{
    
    private final RecipeManager mgr;
    
    public RecipesUpdatedEvent(RecipeManager mgr)
    {
        this.mgr = mgr;
    }

    /**
     * @return The newly-updated recipe manager that now contains all the recipes that were just received.
     */
    public RecipeManager getRecipeManager()
    {
        return mgr;
    }
}
