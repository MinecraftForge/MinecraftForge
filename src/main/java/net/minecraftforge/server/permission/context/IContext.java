/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.server.permission.context;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Use {@link BlockPosContext} or {@link PlayerContext} when possible
 */
public interface IContext
{
    /**
     * World from where permission is requested. Can be null
     */
    @Nullable
    World getWorld();

    /**
     * @return Player requesting permission. Can be null
     */
    @Nullable
    PlayerEntity getPlayer();

    /**
     * @param key Context key
     * @return Context object
     */
    @Nullable
    <T> T get(ContextKey<T> key);

    /**
     * @param key Context key
     * @return true if context contains this key
     */
    boolean has(ContextKey<?> key);
}
