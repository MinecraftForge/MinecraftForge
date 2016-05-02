package net.minecraftforge.common.util;

import java.io.Serializable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

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

    public final BlockPos pos;
    public final int dimId;
    public transient IBlockState replacedBlock;
    public int flag;
    private final NBTTagCompound nbt;
    public transient World world;
    public final UniqueIdentifier blockIdentifier;
    public final int meta;

    public BlockSnapshot(World world, BlockPos pos, IBlockState state)
    {
        this.world = world;
        this.dimId = world.provider.getDimensionId();
        this.pos = pos;
        this.replacedBlock = state;
        this.blockIdentifier = GameRegistry.findUniqueIdentifierFor(state.getBlock());
        this.meta = state.getBlock().getMetaFromState(state);
        this.flag = 3;
        TileEntity te = world.getTileEntity(pos);
        if (te != null)
        {
            nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
        }
        else nbt = null;
        if (DEBUG)
        {
            System.out.printf("Created BlockSnapshot - [World: %s ][Location: %d,%d,%d ][Block: %s ][Meta: %d ]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), blockIdentifier, meta);
        }
    }

    public BlockSnapshot(World world, BlockPos pos, IBlockState state, NBTTagCompound nbt)
    {
        this.world = world;
        this.dimId = world.provider.getDimensionId();
        this.pos = pos.getImmutable();
        this.replacedBlock = state;
        this.blockIdentifier = GameRegistry.findUniqueIdentifierFor(state.getBlock());
        this.meta = state.getBlock().getMetaFromState(state);
        this.flag = 3;
        this.nbt = nbt;
        if (DEBUG)
        {
            System.out.printf("Created BlockSnapshot - [World: %s ][Location: %d,%d,%d ][Block: %s ][Meta: %d ]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), blockIdentifier, meta);
        }
    }

    public BlockSnapshot(World world, BlockPos pos, IBlockState state, int flag)
    {
        this(world, pos, state);
        this.flag = flag;
    }

    /**
     * Raw constructor designed for serialization usages.
     */
    public BlockSnapshot(int dimension, BlockPos pos, String modid, String blockName, int meta, int flag, NBTTagCompound nbt)
    {
        this.dimId = dimension;
        this.pos = pos.getImmutable();
        this.flag = flag;
        this.blockIdentifier = new UniqueIdentifier(modid + ":" + blockName);
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
        return world.getBlockState(pos);
    }

    public World getWorld()
    {
        if (this.world == null)
        {
            this.world = DimensionManager.getWorld(dimId);
        }
        return this.world;
    }

    public IBlockState getReplacedBlock()
    {
        if (this.replacedBlock == null)
        {
            this.replacedBlock = GameRegistry.findBlock(this.blockIdentifier.modId, this.blockIdentifier.name).getStateFromMeta(meta);
        }
        return this.replacedBlock;
    }

    public TileEntity getTileEntity()
    {
        if (nbt != null)
            return TileEntity.createAndLoadEntity(nbt);
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
                world.setBlockState(pos, replaced, applyPhysics ? 3 : 2);
            }
            else
            {
                return false;
            }
        }

        world.setBlockState(pos, replaced, applyPhysics ? 3 : 2);
        world.markBlockForUpdate(pos);
        TileEntity te = null;
        if (nbt != null)
        {
            te = world.getTileEntity(pos);
            if (te != null)
            {
                te.readFromNBT(nbt);
                te.markDirty();
            }
        }

        if (DEBUG)
        {
            System.out.printf("Restored BlockSnapshot with data [World: %s ][Location: %d,%d,%d ][Meta: %d ][Block: %s ][TileEntity: %s ][force: %s ][applyPhysics: %s]", world.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ(), replaced.getBlock().getMetaFromState(replaced), replaced.getBlock().delegate.name(), te, force, applyPhysics);
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
        world.markBlockForUpdate(pos);
        TileEntity te = null;
        if (nbt != null)
        {
            te = world.getTileEntity(pos);
            if (te != null)
            {
                te.readFromNBT(nbt);
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
        compound.setString("blockMod", blockIdentifier.modId);
        compound.setString("blockName", blockIdentifier.name);
        compound.setInteger("posX", pos.getX());
        compound.setInteger("posY", pos.getY());
        compound.setInteger("posZ", pos.getZ());
        compound.setInteger("flag", flag);
        compound.setInteger("dimension", dimId);
        compound.setInteger("metadata", meta);

        compound.setBoolean("hasTE", nbt != null);

        if (nbt != null)
        {
            compound.setTag("tileEntity", nbt);
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
        if (!this.pos.equals(other.pos))
        {
            return false;
        }
        if (this.meta != other.meta)
        {
            return false;
        }
        if (this.dimId != other.dimId)
        {
            return false;
        }
        if (this.nbt != other.nbt && (this.nbt == null || !this.nbt.equals(other.nbt)))
        {
            return false;
        }
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world)))
        {
            return false;
        }
        if (this.blockIdentifier != other.blockIdentifier && (this.blockIdentifier == null || !this.blockIdentifier.equals(other.blockIdentifier)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 73 * hash + this.pos.getX();
        hash = 73 * hash + this.pos.getY();
        hash = 73 * hash + this.pos.getZ();
        hash = 73 * hash + this.meta;
        hash = 73 * hash + this.dimId;
        hash = 73 * hash + (this.nbt != null ? this.nbt.hashCode() : 0);
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + (this.blockIdentifier != null ? this.blockIdentifier.hashCode() : 0);
        return hash;
    }
}