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

import java.lang.ref.WeakReference;
import java.util.Objects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Represents a captured snapshot of a block which will not change
 * automatically.
 * <p>
 * Unlike Block, which only one object can exist per coordinate, BlockSnapshot
 * can exist multiple times for any given Block.
 */
public class BlockSnapshot
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final BlockPos pos;
    private final int dimId;
    @Nullable
    private IBlockState replacedBlock;
    private int flag;
    @Nullable
    private final NBTTagCompound nbt;
    @Nullable
    private WeakReference<World> world;
    private final NBTTagCompound serializedState;

    public BlockSnapshot(World world, BlockPos pos, IBlockState state)
    {
        this(world, pos, state, getTileNBT(world.getTileEntity(pos)));
    }

    public BlockSnapshot(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound nbt)
    {
        this.setWorld(world);
        this.dimId = world.dimension.getId();
        this.pos = pos.toImmutable();
        this.setReplacedBlock(state);
        this.serializedState = NBTUtil.writeBlockState(state);
        this.setFlag(3);
        this.nbt = nbt;
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Created BlockSnapshot - [World: {}][Location: {}][BlockState: {}]", world.getWorldInfo().getWorldName(), pos, getSerializedState());
        }
    }

    public BlockSnapshot(World world, BlockPos pos, IBlockState state, int flag)
    {
        this(world, pos, state);
        this.setFlag(flag);
    }

    /**
     * Raw constructor designed for serialization usages.
     */
    public BlockSnapshot(int dimension, BlockPos pos, NBTTagCompound serializedState, int flag, @Nullable NBTTagCompound nbt)
    {
        this.dimId = dimension;
        this.pos = pos.toImmutable();
        this.setFlag(flag);
        this.serializedState = serializedState;
        this.nbt = nbt;
    }

    public static BlockSnapshot getBlockSnapshot(World world, BlockPos pos)
    {
        return new BlockSnapshot(world, pos, world.getBlockState(pos));
    }

    public static BlockSnapshot getBlockSnapshot(World world, BlockPos pos, int flag)
    {
        return new BlockSnapshot(world, pos, world.getBlockState(pos), flag);
    }

    public static BlockSnapshot readFromNBT(NBTTagCompound tag)
    {
        return new BlockSnapshot(
                tag.getInt("dimension"),
                new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ")),
                tag.getCompound("blockState"),
                tag.getInt("flag"),
                tag.getBoolean("hasTE") ? tag.getCompound("tileEntity") : null);
    }

    @Nullable
    private static NBTTagCompound getTileNBT(@Nullable TileEntity te)
    {
        if (te == null) return null;
        NBTTagCompound nbt = new NBTTagCompound();
        return te.write(nbt);
    }

    public IBlockState getCurrentBlock()
    {
        return getWorld().getBlockState(getPos());
    }

    public World getWorld()
    {
        World world = this.world != null ? this.world.get() : null;
        if (world == null)
        {
            world = ServerLifecycleHooks.getCurrentServer().getWorld(getDimId());
            this.world = new WeakReference<>(world);
        }
        return world;
    }

    public IBlockState getReplacedBlock()
    {
        if (this.replacedBlock == null)
        {
            this.replacedBlock = NBTUtil.readBlockState(this.serializedState);
        }
        return this.replacedBlock;
    }

    @Nullable
    public TileEntity getTileEntity()
    {
        return getNbt() != null ? TileEntity.create(getNbt()) : null;
    }

    public boolean restore()
    {
        return restore(false);
    }

    public boolean restore(boolean force)
    {
        return restore(force, true);
    }

    public boolean restore(boolean force, boolean notifyNeighbors)
    {
        return restoreToLocation(getWorld(), getPos(), force, notifyNeighbors);
    }

    public boolean restoreToLocation(World world, BlockPos pos, boolean force, boolean notifyNeighbors)
    {
        IBlockState current = getCurrentBlock();
        IBlockState replaced = getReplacedBlock();

        if (current != replaced)
        {
            if (force)
            {
                world.setBlockState(pos, replaced, notifyNeighbors ? 3 : 2);
            }
            else
            {
                return false;
            }
        }

        world.setBlockState(pos, replaced, notifyNeighbors ? 3 : 2);

        TileEntity te = null;
        if (getNbt() != null)
        {
            te = world.getTileEntity(pos);
            if (te != null)
            {
                te.read(getNbt());
                te.markDirty();
            }
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Restored BlockSnapshot with data [World: {}][Location: {}][State: {}][Block: {}][TileEntity: {}][force: {}][notifyNeighbors: {}]", world.getWorldInfo().getWorldName(), pos, replaced, replaced.getBlock().delegate.name(), te, force, notifyNeighbors);
        }
        return true;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("blockState", getSerializedState());
        compound.setInt("posX", getPos().getX());
        compound.setInt("posY", getPos().getY());
        compound.setInt("posZ", getPos().getZ());
        compound.setInt("flag", getFlag());
        compound.setInt("dimension", getDimId());

        compound.setBoolean("hasTE", getNbt() != null);

        if (getNbt() != null)
        {
            compound.setTag("tileEntity", getNbt());
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BlockSnapshot other = (BlockSnapshot) obj;
        if (this.getDimId() != other.getDimId())
        {
            return false;
        }
        if (!this.getPos().equals(other.getPos()))
        {
            return false;
        }
        if (!this.getSerializedState().equals(other.getSerializedState()))
        {
            return false;
        }
        if (!Objects.equals(this.getNbt(), other.getNbt()))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 73 * hash + this.getSerializedState().hashCode();
        hash = 73 * hash + this.getDimId();
        hash = 73 * hash + this.getPos().hashCode();
        hash = 73 * hash + Objects.hashCode(this.getNbt());
        return hash;
    }

    public BlockPos getPos() { return pos; }

    public int getDimId() { return dimId; }

    public void setReplacedBlock(IBlockState replacedBlock) { this.replacedBlock = replacedBlock; }

    public int getFlag() { return flag; }

    public void setFlag(int flag) { this.flag = flag; }

    @Nullable
    public NBTTagCompound getNbt() { return nbt; }

    public void setWorld(World world) { this.world = new WeakReference<>(world); }

    /**
     * @return The blockstate in serialized form, via {@link NBTUtil#writeBlockState}
     */
    public NBTTagCompound getSerializedState() { return serializedState; }
}
