/*
 * Minecraft Forge
 * Copyright (c) 2017.
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

/**
 * Interface for handling the placement of entities during dimension change.
 */
public interface ITeleporter
{
    /**
     * Called to handle placing the entity in the new world.
     * (e.g. finding a safe place, adding a way back, etc)
     *
     * @param entity the entity to be placed
     * @param yaw suggested yaw value
     */
    void placeEntity(Entity entity, float yaw);
}
