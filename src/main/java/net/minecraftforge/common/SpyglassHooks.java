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

package net.minecraftforge.common;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpyglassHooks
{

    private static final List<Predicate<Player>> SPYGLASS_PREDICATES = new ArrayList<>();

    /**
     * Registers a predicate which will be checked for whether the player is considered to be 'scoping' with a spyglass.
     * The predicate will only be tested on the client.
     * Only one registered predicate needs to be true for scoping to occur.
     * <strong>The passed predicate will be tested very frequently (typically every frame).</strong>
     *
     * @param spyglassCondition A predicate which will be checked on the client side.
     */
    public static synchronized void registerSpyglassCondition(Predicate<Player> spyglassCondition)
    {
        SPYGLASS_PREDICATES.add(spyglassCondition);
    }

    public static boolean isPlayerScoping(Player player)
    {
        for (Predicate<Player> spyglassPredicate : SPYGLASS_PREDICATES)
        {
            if (spyglassPredicate.test(player))
            {
                return true;
            }
        }
        return false;
    }
}
