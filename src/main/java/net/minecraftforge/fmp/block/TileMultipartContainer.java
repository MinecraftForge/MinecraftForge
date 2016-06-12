package net.minecraftforge.fmp.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.IWorldLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.capabilities.CapabilityMultipartContainer;
import net.minecraftforge.fmp.capabilities.CapabilitySlottedCapProvider;
import net.minecraftforge.fmp.capabilities.MultipartCapabilityHelper;
import net.minecraftforge.fmp.client.multipart.MultipartRegistryClient;
import net.minecraftforge.fmp.client.multipart.MultipartSpecialRenderer;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.IMultipartContainer;
import net.minecraftforge.fmp.multipart.Multipart;
import net.minecraftforge.fmp.multipart.MultipartContainer;
import net.minecraftforge.fmp.multipart.MultipartContainer.IMultipartContainerListener;

/**
 * A final class that extends {@link BlockContainer} and provides the {@link IMultipartContainer} {@link Capability}. 
 * Represents a TileEntity which can contain any kind of multipart.<br/>
 * <b>You do NOT need to extend this class for your multiparts to work.</b> I repeat, you do NOT. You need to either
 * extend {@link Multipart} or implement {@link IMultipart}. If you only need microblock support, look into
 * {@link BlockCoverable}.
 */
public class TileMultipartContainer extends TileEntity implements IWorldLocation, IMultipartContainerListener
{

    protected MultipartContainer container;

    public TileMultipartContainer(MultipartContainer container)
    {
        this.container = new MultipartContainer(this, container.canTurnIntoBlock(), container);
        this.container.setListener(this);
    }

    public TileMultipartContainer()
    {
        this.container = new MultipartContainer(this, true);
        this.container.setListener(this);
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

    public MultipartContainer getPartContainer()
    {
        return container;
    }

    @Override
    public void onAddPartPre(IMultipart part)
    {
        if (getWorld() != null && part instanceof ITickable && !(this instanceof ITickable))
        {
            getWorld().setBlockState(getPos(),
                    ForgeMultipartModContainer.multipart.getDefaultState().withProperty(BlockMultipartContainer.PROPERTY_TICKING, true));
            TileEntity te = getWorld().getTileEntity(getPos());
            if (te != null && te instanceof TileMultipartContainer)
            {
                ((TileMultipartContainer) te).container = container;
                container.setListener((TileMultipartContainer) te);
            }
            else
            {
                throw new RuntimeException("Failed to replace ticking tile!");
            }
        }
    }

    @Override
    public void onAddPartPost(IMultipart part)
    {
    }

    @Override
    public void onRemovePartPre(IMultipart part)
    {
        if (getWorld() != null && part instanceof ITickable && container.getParts().size() > 1)
        {
            boolean shouldTick = false;
            for (IMultipart p : container.getParts())
            {
                if (p != part && p instanceof ITickable)
                {
                    shouldTick = true;
                    break;
                }
            }
            if (!shouldTick)
            {
                getWorld().setBlockState(getPos(), ForgeMultipartModContainer.multipart.getDefaultState()
                        .withProperty(BlockMultipartContainer.PROPERTY_TICKING, false));
                TileEntity te = getWorld().getTileEntity(getPos());
                if (te != null && te instanceof TileMultipartContainer)
                {
                    ((TileMultipartContainer) te).container = container;
                    container.setListener((TileMultipartContainer) te);
                }
                else
                {
                    throw new RuntimeException("Failed to replace ticking tile!");
                }
            }
        }
    }

    @Override
    public void onRemovePartPost(IMultipart part)
    {
        getWorld().setBlockState(getPos(), net.minecraft.init.Blocks.AIR.getDefaultState(), getWorld().isRemote ? 11 : 3);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityMultipartContainer.MULTIPART_CONTAINER_CAPABILITY
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
        if (capability == CapabilitySlottedCapProvider.SLOTTED_CAP_PROVIDER_CAPABILITY)
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
    public void onLoad()
    {
        super.onLoad();
        for (IMultipart part : container.getParts())
        {
            part.onLoaded();
        }
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
        for (IMultipart part : container.getParts())
        {
            part.onUnloaded();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        container.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        container.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return container.writeToNBT(super.getUpdateTag());
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
        AxisAlignedBB bounds = null;
        for (IMultipart part : container.getParts())
        {
            AxisAlignedBB bb = part.getRenderBoundingBox();
            if (bb != null)
            {
                if (bounds == null)
                {
                    bounds = bb;
                }
                else
                {
                    bounds = bounds.union(bb);
                }
            }
        }
        if (bounds == null)
        {
            bounds = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
        }
        return bounds.offset(getPosIn().getX(), getPosIn().getY(), getPosIn().getZ());
    }

    @Override
    public boolean hasFastRenderer()
    {
        for (IMultipart part : container.getParts())
        {
            if (getSpecialRenderer(part) != null && !part.hasFastRenderer())
            {
                return false;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private MultipartSpecialRenderer<?> getSpecialRenderer(IMultipart part)
    {
        return MultipartRegistryClient.getSpecialRenderer(part);
    }

    public static class Ticking extends TileMultipartContainer implements ITickable
    {
        @Override
        public void update()
        {
            for (IMultipart part : container.getTickingParts())
            {
                if (part instanceof ITickable)
                {
                    ((ITickable) part).update();
                }
            }
        }

    }

}
