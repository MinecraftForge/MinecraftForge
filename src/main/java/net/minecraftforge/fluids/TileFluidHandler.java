
package net.minecraftforge.fluids;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import edu.umd.cs.findbugs.annotations.Nullable;

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
    public int fill(@Nullable EnumFacing from, FluidStack resource, boolean doFill)
    {
        if(from==null)
        	from=EnumFacing.NORTH;
    	return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(@Nullable EnumFacing from, FluidStack resource, boolean doDrain)
    {
    	if(from==null)
        	from=EnumFacing.NORTH;
    	if (resource == null || !resource.isFluidEqual(tank.getFluid()))
        {
            return null;
        }
        return tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(@Nullable EnumFacing from, int maxDrain, boolean doDrain)
    {
    	if(from==null)
        	from=EnumFacing.NORTH;
    	return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(@Nullable EnumFacing from, Fluid fluid)
    {
    	if(from==null)
        	from=EnumFacing.NORTH;
    	return true;
    }

    @Override
    public boolean canDrain(@Nullable EnumFacing from, Fluid fluid)
    {
    	if(from==null)
        	from=EnumFacing.NORTH;
    	return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(@Nullable EnumFacing from)
    {
    	if(from==null)
        	from=EnumFacing.NORTH;
    	return new FluidTankInfo[] { tank.getInfo() };
    }
}
