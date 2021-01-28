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

package net.minecraftforge.common.util;

import java.lang.ref.WeakReference;
import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockSnapshot", "false"));

    private final RegistryKey<World> dim;
    private final BlockPos pos;
    private final int flags;
    private final BlockState block;
    @Nullable
    private final CompoundNBT nbt;

    @Nullable
    private WeakReference<IWorld> world;
    private String toString = null;

    private BlockSnapshot(RegistryKey<World> dim, IWorld world, BlockPos pos, BlockState state, @Nullable CompoundNBT nbt, int flags)
    {
        this.dim = dim;
        this.pos = pos.toImmutable();
        this.block = state;
        this.flags = flags;
        this.nbt = nbt;

        this.world = new WeakReference<>(world);

        if (DEBUG)
            System.out.println("Created " + this.toString());
    }

    public static BlockSnapshot create(RegistryKey<World> dim, IWorld world, BlockPos pos)
    {
        return create(dim, world, pos, 3);
    }

    public static BlockSnapshot create(RegistryKey<World> dim, IWorld world, BlockPos pos, int flag)
    {
        return new BlockSnapshot(dim, world, pos, world.getBlockState(pos), getTileNBT(world.getTileEntity(pos)), flag);
    }

    @Nullable
    private static CompoundNBT getTileNBT(@Nullable TileEntity te)
    {
        return te == null ? null : te.write(new CompoundNBT());
    }

    public BlockState getCurrentBlock()
    {
        IWorld world = getWorld();
        return world == null ? Blocks.AIR.getDefaultState() : world.getBlockState(this.pos);
    }

    @Nullable
    public IWorld getWorld()
    {
        IWorld world = this.world != null ? this.world.get() : null;
        if (world == null)
        {
            world = ServerLifecycleHooks.getCurrentServer().getWorld(this.dim);
            this.world = new WeakReference<IWorld>(world);
        }
        return world;
    }

    public BlockState getReplacedBlock()
    {
        return this.block;
    }

    @Nullable
    public TileEntity getTileEntity()
    {
        return getNbt() != null ? TileEntity.readTileEntity(getReplacedBlock(), getNbt()) : null;
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

    public boolean restoreToLocation(IWorld world, BlockPos pos, boolean force, boolean notifyNeighbors)
    {
        BlockState current = getCurrentBlock();
        BlockState replaced = getReplacedBlock();

        int flags = notifyNeighbors ? Constants.BlockFlags.DEFAULT : Constants.BlockFlags.BLOCK_UPDATE;

        if (current != replaced)
        {
            if (force)
                world.setBlockState(pos, replaced, flags);
            else
                return false;
        }

        world.setBlockState(pos, replaced, flags);
        if (world instanceof World)
            ((World)world).notifyBlockUpdate(pos, current, replaced, flags);

        TileEntity te = null;
        if (getNbt() != null)
        {
            te = world.getTileEntity(pos);
            if (te != null)
            {
                te.read(getReplacedBlock(), getNbt());
                te.markDirty();
            }
        }

        if (DEBUG)
            System.out.println("Restored " + this.toString());
        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        final BlockSnapshot other = (BlockSnapshot) obj;
        return this.dim.equals(other.dim) &&
            this.pos.equals(other.pos) &&
            this.block == other.block &&
            this.flags == other.flags &&
            Objects.equals(this.nbt, other.nbt);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 73 * hash + this.dim.hashCode();
        hash = 73 * hash + this.pos.hashCode();
        hash = 73 * hash + this.block.hashCode();
        hash = 73 * hash + this.flags;
        hash = 73 * hash + Objects.hashCode(this.getNbt());
        return hash;
    }

    @Override
    public String toString()
    {
        if (toString == null)
        {
            this.toString =
                "BlockSnapshot[" +
                "World:" + this.dim.getLocation() + ',' +
                "Pos: " + this.pos + ',' +
                "State: " + this.block + ',' +
                "Flags: " + this.flags + ',' +
                "NBT: " + (this.nbt == null ? "null" : this.nbt.toString()) +
                ']';
        }
        return this.toString;
    }

    public BlockPos getPos() { return pos; }


    public int getFlag() { return flags; }

    @Nullable
    public CompoundNBT getNbt() { return nbt; }

}
