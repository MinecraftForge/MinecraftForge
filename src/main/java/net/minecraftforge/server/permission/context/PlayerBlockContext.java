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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerBlockContext extends PlayerContext
{
    private final BlockPos blockPos;

    public PlayerBlockContext(@Nonnull EntityPlayer ep, @Nonnull BlockPos pos)
    {
        super(ep);
        Preconditions.checkNotNull(pos, "BlockPos can't be null in PlayerBlockContext!");
        blockPos = pos;
    }

    @Override
    public <T> T get(@Nonnull ContextKey<T> key)
    {
        if(key.equals(ContextKey.BLOCK))
        {
            return (T) blockPos;
        }
        else if(key.equals(ContextKey.CHUNK))
        {
            ChunkPos chunkPos = super.get(ContextKey.CHUNK);
            return (T) (chunkPos == null ? new ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4) : chunkPos);
        }

        return super.get(key);
    }

    @Override
    public boolean has(@Nonnull ContextKey<?> key)
    {
        return key.equals(ContextKey.BLOCK) || super.has(key);
    }

    @Nonnull
    @Override
    public <T> Context set(@Nonnull ContextKey<T> key, @Nullable T obj)
    {
        return key.equals(ContextKey.BLOCK) ? this : super.set(key, obj);
    }
}