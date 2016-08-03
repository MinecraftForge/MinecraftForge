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

package net.minecraftforge.server.permission.context;

import com.google.common.base.Preconditions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class ContextKey<T>
{
    public static final ContextKey<World> WORLD = new ContextKey<World>("world");
    public static final ContextKey<EntityPlayer> PLAYER = new ContextKey<EntityPlayer>("player");
    public static final ContextKey<BlockPos> BLOCK = new ContextKey<BlockPos>("block");
    public static final ContextKey<ChunkPos> CHUNK = new ContextKey<ChunkPos>("chunk");
    public static final ContextKey<Entity> ENTITY = new ContextKey<Entity>("entity");

    private final String key;

    public ContextKey(@Nonnull String key)
    {
        Preconditions.checkNotNull(key, "Context key can't be null!");

        if(key.isEmpty())
        {
            throw new IllegalArgumentException("Context key can't be empty!");
        }

        this.key = key;
    }

    public int hashCode()
    {
        return key.hashCode();
    }

    public String toString()
    {
        return key;
    }

    public boolean equals(Object o)
    {
        return o == this || (o != null && o.toString().equals(key));
    }
}