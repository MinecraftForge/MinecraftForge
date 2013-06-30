package net.minecraftforge.common;


import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockSnapshot {
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final int blockID;
    public final int meta;
    private final NBTTagCompound nbt;

    public BlockSnapshot(World world, int x, int y, int z, int meta) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockID = world.getBlockId(x, y, z);
        this.meta = meta;
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null)
        {
            nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
        }
        else nbt = null;
    }

    public Block getBlock() {
        return Block.blocksList[world.getBlockId(x, y, z)];
    }

    public TileEntity getTileEntity() {
        if (nbt != null)
            return TileEntity.createAndLoadEntity(nbt);
        else return null;
    }

    public boolean restore() {
        return restore(false);
    }

    public boolean restore(boolean force) {
        return restore(force, true);
    }

    public boolean restore(boolean force, boolean applyPhysics) {
        Block block = getBlock();

        if (block != Block.blocksList[blockID] || world.getBlockMetadata(x & 15, y, z & 15) != meta) {
            if (force) {
                world.setBlock(x, y, z, blockID, meta, applyPhysics ? 3 : 2);
            } else {
                return false;
            }
        }

        world.setBlockMetadataWithNotify(x, y, z, meta, applyPhysics ? 3 : 2);
        world.markBlockForUpdate(x, y, z);
        if (nbt != null)
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (te != null)
            {
                te.readFromNBT(nbt);
            }
        }

        return true;
    }

    public boolean restoreToLocation(World world, int x, int y, int z, boolean force, boolean applyPhysics) {
        Block block = getBlock();

        if (block != Block.blocksList[blockID] || world.getBlockMetadata(x & 15, y, z & 15) != meta) {
            if (force) {
                world.setBlock(x, y, z, blockID, meta, applyPhysics ? 3 : 2);
            } else {
                return false;
            }
        }

        world.setBlockMetadataWithNotify(x, y, z, meta, applyPhysics ? 3 : 2);
        world.markBlockForUpdate(x, y, z);
        if (nbt != null)
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (te != null)
            {
                te.readFromNBT(nbt);
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockSnapshot other = (BlockSnapshot) obj;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world))) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        if (this.blockID != other.blockID) {
            return false;
        }
        if (this.meta != other.meta) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 73 * hash + this.x;
        hash = 73 * hash + this.y;
        hash = 73 * hash + this.z;
        hash = 73 * hash + this.blockID;
        hash = 73 * hash + this.meta;
        return hash;
    }
}