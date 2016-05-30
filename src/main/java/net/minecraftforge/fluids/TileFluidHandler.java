
package net.minecraftforge.fluids;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;

/**
 * Reference Tile Entity implementation of {@link IFluidHandler}. Use/extend this or write your own.
 * @deprecated see {@link net.minecraftforge.fluids.capability.TileFluidHandler}
 */
@Deprecated
public class TileFluidHandler extends TileEntity implements IFluidHandler
{
    protected FluidTank tank = new FluidTank(Fluid.BUCKET_VOLUME);

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag = super.writeToNBT(tag);
        tank.writeToNBT(tag);
        return tag;
    }

    /* IFluidHandler */
    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill)
    {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(tank.getFluid()))
        {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
    {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from)
    {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return super.hasCapability(capability, facing) || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (super.hasCapability(capability, facing))
        {
            return super.getCapability(capability, facing);
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidHandlerWrapper(this, facing));
        }
        return null;
    }
}
