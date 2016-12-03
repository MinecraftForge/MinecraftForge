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

package net.minecraftforge.common.util;

import java.io.Serializable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Represents a captured snapshot of a block which will not change
 * automatically.
 * <p>
 * Unlike Block, which only one object can exist per coordinate, BlockSnapshot
 * can exist multiple times for any given Block.
 */
@SuppressWarnings({"serial", "deprecation"})
public class BlockSnapshot implements Serializable
{
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugBlockSnapshot", "false"));

    private final BlockPos pos;
    private final int dimId;
    private transient IBlockState replacedBlock;
    private int flag;
    private final NBTTagCompound nbt;
    private transient World world;
    private final ResourceLocation registryName;
    private final int meta;

    public BlockSnapshot(World world, BlockPos pos, IBlockState state)
    {
        this.setWorld(world);
        this.dimId = world.provider.getDimension();
        this.pos = pos;
        this.setReplacedBlock(state);
        this.registryName = state.getBlock().getRegistryName();
        this.meta = state.getBlock().getMetaFromState(state);
        this.setFlag(3);
        TileEntity te = world.getTileEntity(pos);
        if (te != null)
        {
            nbt = new NBTTagCompound();
            te.writeToNBT(getNbt());
        }
        else nbt = null;
        if (DEBUG)
        {
            System.out.printf("Created BlockSnapshot - [World: %s ][Location: %d,%d,%d ][Block: %s ][Meta: %d ]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), getRegistryName(), getMeta());
        }
    }

    public BlockSnapshot(World world, BlockPos pos, IBlockState state, NBTTagCompound nbt)
    {
        this.setWorld(world);
        this.dimId = world.provider.getDimension();
        this.pos = pos.toImmutable();
        this.setReplacedBlock(state);
        this.registryName = state.getBlock().getRegistryName();
        this.meta = state.getBlock().getMetaFromState(state);
        this.setFlag(3);
        this.nbt = nbt;
        if (DEBUG)
        {
            System.out.printf("Created BlockSnapshot - [World: %s ][Location: %d,%d,%d ][Block: %s ][Meta: %d ]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), getRegistryName(), getMeta());
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
    public BlockSnapshot(int dimension, BlockPos pos, String modId, String blockName, int meta, int flag, NBTTagCompound nbt)
    {
        this.dimId = dimension;
        this.pos = pos.toImmutable();
        this.setFlag(flag);
        this.registryName = new ResourceLocation(modId, blockName);
        this.meta = meta;
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
        NBTTagCompound nbt = tag.getBoolean("hasTE") ? null : tag.getCompoundTag("tileEntity");

        return new BlockSnapshot(
                tag.getInteger("dimension"),
                new BlockPos(tag.getInteger("posX"), tag.getInteger("posY"), tag.getInteger("posZ")),
                tag.getString("blockMod"),
                tag.getString("blockName"),
                tag.getInteger("metadata"),
                tag.getInteger("flag"),
                nbt);
    }

    public IBlockState getCurrentBlock()
    {
        return getWorld().getBlockState(getPos());
    }

    public World getWorld()
    {
        if (this.world == null)
        {
            this.world = DimensionManager.getWorld(getDimId());
        }
        return this.world;
    }

    public IBlockState getReplacedBlock()
    {
        if (this.replacedBlock == null)
        {
            this.replacedBlock = GameRegistry.findBlock(this.getRegistryName().getResourceDomain(), this.getRegistryName().getResourcePath()).getStateFromMeta(getMeta());
        }
        return this.replacedBlock;
    }

    public TileEntity getTileEntity()
    {
        if (getNbt() != null)
            return TileEntity.create(getWorld(), getNbt());
        else return null;
    }

    public boolean restore()
    {
        return restore(false);
    }

    public boolean restore(boolean force)
    {
        return restore(force, true);
    }

    public boolean restore(boolean force, boolean applyPhysics)
    {
        IBlockState current = getCurrentBlock();
        IBlockState replaced = getReplacedBlock();
        if (current.getBlock() != replaced.getBlock() || current.getBlock().getMetaFromState(current) != replaced.getBlock().getMetaFromState(replaced))
        {
            if (force)
            {
                getWorld().setBlockState(getPos(), replaced, applyPhysics ? 3 : 2);
            }
            else
            {
                return false;
            }
        }

        getWorld().setBlockState(getPos(), replaced, applyPhysics ? 3 : 2);
        getWorld().notifyBlockUpdate(getPos(), current, replaced, applyPhysics ? 3 : 2);
        TileEntity te = null;
        if (getNbt() != null)
        {
            te = getWorld().getTileEntity(getPos());
            if (te != null)
            {
                te.readFromNBT(getNbt());
                te.markDirty();
            }
        }

        if (DEBUG)
        {
            System.out.printf("Restored BlockSnapshot with data [World: %s ][Location: %d,%d,%d ][Meta: %d ][Block: %s ][TileEntity: %s ][force: %s ][applyPhysics: %s]", getWorld().getWorldInfo().getWorldName(), getPos().getX(), getPos().getY(), getPos().getZ(), replaced.getBlock().getMetaFromState(replaced), replaced.getBlock().delegate.name(), te, force, applyPhysics);
        }
        return true;
    }

    public boolean restoreToLocation(World world, BlockPos pos, boolean force, boolean applyPhysics)
    {
        IBlockState current = getCurrentBlock();
        IBlockState replaced = getReplacedBlock();
        if (current.getBlock() != replaced.getBlock() || current.getBlock().getMetaFromState(current) != replaced.getBlock().getMetaFromState(replaced))
        {
            if (force)
            {
                world.setBlockState(pos, replaced, applyPhysics ? 3 : 2);
            }
            else
            {
                return false;
            }
        }

        world.setBlockState(pos, replaced, applyPhysics ? 3 : 2);
        world.notifyBlockUpdate(pos, current, replaced, applyPhysics ? 3 : 2);
        TileEntity te = null;
        if (getNbt() != null)
        {
            te = world.getTileEntity(pos);
            if (te != null)
            {
                te.readFromNBT(getNbt());
                te.markDirty();
            }
        }

        if (DEBUG)
        {
            System.out.printf("Restored BlockSnapshot with data [World: %s ][Location: %d,%d,%d ][Meta: %d ][Block: %s ][TileEntity: %s ][force: %s ][applyPhysics: %s]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), replaced.getBlock().getMetaFromState(replaced), replaced.getBlock().delegate.name(), te, force, applyPhysics);
        }
        return true;
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setString("blockMod", getRegistryName().getResourceDomain());
        compound.setString("blockName", getRegistryName().getResourcePath());
        compound.setInteger("posX", getPos().getX());
        compound.setInteger("posY", getPos().getY());
        compound.setInteger("posZ", getPos().getZ());
        compound.setInteger("flag", getFlag());
        compound.setInteger("dimension", getDimId());
        compound.setInteger("metadata", getMeta());

        compound.setBoolean("hasTE", getNbt() != null);

        if (getNbt() != null)
        {
            compound.setTag("tileEntity", getNbt());
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BlockSnapshot other = (BlockSnapshot) obj;
        if (!this.getPos().equals(other.getPos()))
        {
            return false;
        }
        if (this.getMeta() != other.getMeta())
        {
            return false;
        }
        if (this.getDimId() != other.getDimId())
        {
            return false;
        }
        if (this.getNbt() != other.getNbt() && (this.getNbt() == null || !this.getNbt().equals(other.getNbt())))
        {
            return false;
        }
        if (this.getWorld() != other.getWorld() && (this.getWorld() == null || !this.getWorld().equals(other.getWorld())))
        {
            return false;
        }
        if (this.getRegistryName() != other.getRegistryName() && (this.getRegistryName() == null || !this.getRegistryName().equals(other.getRegistryName())))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 73 * hash + this.getPos().getX();
        hash = 73 * hash + this.getPos().getY();
        hash = 73 * hash + this.getPos().getZ();
        hash = 73 * hash + this.getMeta();
        hash = 73 * hash + this.getDimId();
        hash = 73 * hash + (this.getNbt() != null ? this.getNbt().hashCode() : 0);
        hash = 73 * hash + (this.getWorld() != null ? this.getWorld().hashCode() : 0);
        hash = 73 * hash + (this.getRegistryName() != null ? this.getRegistryName().hashCode() : 0);
        return hash;
    }

    public BlockPos getPos() { return pos; }
    public int getDimId() { return dimId; }
    public void setReplacedBlock(IBlockState replacedBlock) { this.replacedBlock = replacedBlock; }
    public int getFlag() { return flag; }
    public void setFlag(int flag) { this.flag = flag; }
    public NBTTagCompound getNbt() { return nbt; }
    public void setWorld(World world) { this.world = world; }
    public ResourceLocation getRegistryName() { return registryName; }
    public int getMeta() { return meta; }
}