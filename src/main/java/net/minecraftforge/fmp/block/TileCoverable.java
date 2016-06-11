package net.minecraftforge.fmp.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fmp.capabilities.CapabilityMicroblockContainerTile;
import net.minecraftforge.fmp.capabilities.CapabilityMultipartContainer;
import net.minecraftforge.fmp.capabilities.CapabilitySlottedCapProvider;
import net.minecraftforge.fmp.capabilities.ISlottedCapabilityProvider;
import net.minecraftforge.fmp.capabilities.MultipartCapabilityHelper;
import net.minecraftforge.fmp.microblock.IMicroblock;
import net.minecraftforge.fmp.microblock.IMicroblockContainerTile;
import net.minecraftforge.fmp.microblock.MicroblockContainer;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.PartSlot;

/**
 * An implementation of {@link TileMultipartContainer} and {@link IMicroblockContainerTile} that acts as a microblock
 * container.<br/>
 * Extend this class if you want a custom TileEntity for your {@link BlockCoverable}.
 */
public class TileCoverable extends TileEntity implements IMicroblockContainerTile, ISlottedCapabilityProvider
{

    private MicroblockContainer container;

    public TileCoverable()
    {
    }

    @Override
    public World getWorldIn()
    {
        return getWorld();
    }

    @Override
    public BlockPos getPosIn()
    {
        return getPos();
    }

    @Override
    public MicroblockContainer getMicroblockContainer()
    {
        return container != null ? container : (container = new MicroblockContainer(this));
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock)
    {
        return true;
    }

    @Override
    public void onMicroblocksChanged()
    {
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityMultipartContainer.MULTIPART_CONTAINER_CAPABILITY
                || capability == CapabilityMicroblockContainerTile.MICROBLOCK_CONTAINER_CAPABILITY
                || capability == CapabilitySlottedCapProvider.SLOTTED_CAP_PROVIDER_CAPABILITY)
        {
            return true;
        }
        if (MultipartCapabilityHelper.hasCapability(container, capability, facing))
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityMultipartContainer.MULTIPART_CONTAINER_CAPABILITY)
        {
            return (T) container;
        }
        if (capability == CapabilityMicroblockContainerTile.MICROBLOCK_CONTAINER_CAPABILITY
                || capability == CapabilitySlottedCapProvider.SLOTTED_CAP_PROVIDER_CAPABILITY)
        {
            return (T) this;
        }
        T impl = MultipartCapabilityHelper.getCapability(container, capability, facing);
        if (impl != null)
        {
            return impl;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, PartSlot slot, EnumFacing facing)
    {
        return container.hasCapability(capability, slot, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, PartSlot slot, EnumFacing facing)
    {
        return container.getCapability(capability, slot, facing);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        for (IMultipart part : getMicroblockContainer().getParts())
        {
            part.onLoaded();
        }
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        for (IMultipart part : getMicroblockContainer().getParts())
        {
            part.onUnloaded();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        getMicroblockContainer().getPartContainer().writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        getMicroblockContainer().getPartContainer().readFromNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return getMicroblockContainer().getPartContainer().writeToNBT(super.getUpdateTag());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bounds = super.getRenderBoundingBox().offset(-getPosIn().getX(), -getPosIn().getY(), -getPosIn().getZ());
        for (IMultipart part : getMicroblockContainer().getParts())
        {
            AxisAlignedBB bb = part.getRenderBoundingBox();
            if (bb != null)
            {
                bounds = bounds.union(bb);
            }
        }
        return bounds.offset(getPosIn().getX(), getPosIn().getY(), getPosIn().getZ());
    }
}
