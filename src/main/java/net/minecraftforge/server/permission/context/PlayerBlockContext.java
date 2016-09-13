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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class PlayerBlockContext extends PlayerContext
{
    private final BlockPos blockPos;
    private IBlockState blockState;

    public PlayerBlockContext(EntityPlayerMP ep, BlockPos pos, @Nullable IBlockState state)
    {
        super(ep);
        blockPos = Preconditions.checkNotNull(pos, "BlockPos can't be null in PlayerBlockContext!");
        blockState = state;
    }

    @Override
    public <T> T get(String key)
    {
        if(key.equals(ContextKeys.BLOCK_POS))
        {
            return (T) blockPos;
        }
        else if(key.equals(ContextKeys.BLOCK_STATE))
        {
            if(blockState == null)
            {
                blockState = getWorld().getBlockState(blockPos);
            }

            return (T) blockState;
        }
        return super.get(key);
    }

    @Override
    public boolean has(String key)
    {
        return key.equals(ContextKeys.BLOCK_POS) || key.equals(ContextKeys.BLOCK_STATE) || super.has(key);
    }

    @Override
    public <T> IContext set(String key, @Nullable T obj)
    {
        return (key.equals(ContextKeys.BLOCK_POS) || key.equals(ContextKeys.BLOCK_STATE)) ? this : super.set(key, obj);
    }
}