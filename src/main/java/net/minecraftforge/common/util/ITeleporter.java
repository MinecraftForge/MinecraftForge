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

package net.minecraftforge.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

/**
 * Interface for handling the placement of entities during dimension change.
 *
 * An implementation of this interface can be used to place the entity
 * in a safe location, or generate a return portal, for instance.
 *
 * See the {@link net.minecraft.world.Teleporter} class, which has
 * been patched to implement this interface, for a vanilla example.
 */
public interface ITeleporter
{
    /**
     * Called to handle placing the entity in the new world.
     *
     * The initial position of the entity will be its
     * position in the origin world, multiplied horizontally
     * by the computed cross-dimensional movement factor
     * (see {@link WorldProvider#getMovementFactor()}).
     *
     * Note that the supplied entity has not yet been spawned
     * in the destination world at the time.
     *
     * @param world  the entity's destination
     * @param entity the entity to be placed
     * @param yaw    the suggested yaw value to apply
     */
    void placeEntity(World world, Entity entity, float yaw);

    // used internally to handle vanilla hardcoding
    default boolean isVanilla()
    {
        return getClass() == Teleporter.class;
    }
}
