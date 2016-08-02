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

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ParametersAreNonnullByDefault
public final class Context
{
    public static final Context NO_CONTEXT = new Context();

    public static class Key<T>
    {
        private final String key;

        public Key(String key)
        {
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
            return o == this || (o instanceof Key && ((Key) o).key.equals(key));
        }
    }

    public static final Key<World> WORLD = new Key<World>("world");
    public static final Key<EntityPlayer> PLAYER = new Key<EntityPlayer>("player");
    public static final Key<BlockPos> BLOCK = new Key<BlockPos>("block");
    public static final Key<ChunkPos> CHUNK = new Key<ChunkPos>("chunk");
    public static final Key<Entity> ENTITY = new Key<Entity>("entity");
    public static final Key<IBlockState> BLOCK_STATE = new Key<IBlockState>("blockstate");

    private final Map<Key<?>, Object> map = new HashMap<Key<?>, Object>();

    public Context()
    {
    }

    public Context(EntityPlayer e)
    {
        set(WORLD, e.worldObj);
        set(PLAYER, e);
    }

    public Context(World world, BlockPos pos)
    {
        set(WORLD, world);
        set(BLOCK, pos);
    }

    public Context(EntityPlayer e, BlockPos pos)
    {
        this(e);
        set(BLOCK, pos);
    }

    public <T> T get(Key<T> key)
    {
        return (T) map.get(key);
    }

    public boolean has(Key<?> key)
    {
        return map.containsKey(key);
    }

    public <T> Context set(Key<T> key, T obj)
    {
        map.put(key, obj);
        return this;
    }

    public Set<Map.Entry<Key<?>, Object>> getContext()
    {
        return Collections.unmodifiableSet(map.entrySet());
    }
}