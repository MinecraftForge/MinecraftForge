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

package net.minecraftforge.server.permission;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public class Context
{
    public static final Context NO_CONTEXT = new Context();

    // Some default custom map keys
    public static final String CHUNK = "chunk";
    public static final String ENTITY = "entity";
    public static final String BLOCK_POS_2 = "block2";
    public static final String BLOCK_STATE = "blockstate";

    private IBlockAccess blockAccess;
    private EntityPlayer player;
    private BlockPos blockPos;
    private Map<String, Object> custom;

    public Context()
    {
    }

    public Context(EntityPlayer e)
    {
        blockAccess = e.worldObj;
        player = e;
    }

    public Context(IBlockAccess w, BlockPos pos)
    {
        blockAccess = w;
        blockPos = pos;
    }

    public Context(EntityPlayer e, BlockPos pos)
    {
        blockAccess = e.worldObj;
        player = e;
        blockPos = pos;
    }

    // IBlockAccess //

    public IBlockAccess getBlockAccess()
    {
        return blockAccess;
    }

    public Context setBlockAccess(IBlockAccess w)
    {
        blockAccess = w;
        return this;
    }

    public boolean hasBlockAccess()
    {
        return blockAccess != null;
    }

    // Entity //

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public Context setPlayer(EntityPlayer e)
    {
        blockAccess = e.worldObj;
        player = e;
        return this;
    }

    public boolean hasPlayer()
    {
        return player != null;
    }

    // BlockPos //

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public Context setBlockPos(BlockPos pos)
    {
        blockPos = pos;
        return this;
    }

    public boolean hasBlockPos()
    {
        return blockPos != null;
    }

    // Custom objects //

    public Object getCustomObject(String id)
    {
        return (custom == null || id.isEmpty()) ? null : custom.get(id);
    }

    public boolean hasCustomObject(String id)
    {
        return custom != null && !id.isEmpty() && custom.containsKey(id);
    }

    public Context setCustomObject(String id, Object obj)
    {
        if(custom == null)
        {
            custom = new HashMap<String, Object>();
        }

        custom.put(id, obj);
        return this;
    }
}