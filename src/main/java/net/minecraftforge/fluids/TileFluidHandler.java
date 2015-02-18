
package net.minecraftforge.fluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Reference Tile Entity implementation of {@link IFluidHandler}. Use/extend this or write your own.
 *
 * @author King Lemming
 *
 */
public class TileFluidHandler extends TileEntity implements IFluidHandler
{
    protected FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
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
}
