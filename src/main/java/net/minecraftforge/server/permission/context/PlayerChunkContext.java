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

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 15.08.2016.
 */
public class PlayerChunkContext extends PlayerContext
{
    private final ChunkPos chunkPos;

    public PlayerChunkContext(EntityPlayer ep, ChunkPos pos)
    {
        super(ep);
        chunkPos = Preconditions.checkNotNull(pos, "ChunkPos can't be null in PlayerChunkContext!");
    }

    public PlayerChunkContext(EntityPlayer ep, BlockPos pos)
    {
        super(ep);
        Preconditions.checkNotNull(pos, "BlockPos can't be null in PlayerChunkContext!");
        chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public <T> T get(String key)
    {
        return (T) (key.equals(ContextKeys.CHUNK) ? chunkPos : super.get(key));
    }

    @Override
    public boolean has(String key)
    {
        return key.equals(ContextKeys.CHUNK) || super.has(key);
    }

    @Override
    public <T> IContext set(String key, @Nullable T obj)
    {
        return key.equals(ContextKeys.CHUNK) ? this : super.set(key, obj);
    }
}