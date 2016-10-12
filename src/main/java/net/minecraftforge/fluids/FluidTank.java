package net.minecraftforge.fluids;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * Reference implementation of {@link IFluidTank}. Use/extend this or implement your own.
 */
public class FluidTank implements IFluidTank, IFluidHandler
{
    @Nullable
    protected FluidStack fluid;
    protected int capacity;
    protected TileEntity tile;
    protected boolean canFill = true;
    protected boolean canDrain = true;
    protected IFluidTankProperties[] tankProperties;

    public FluidTank(int capacity)
    {
        this(null, capacity);
    }

    public FluidTank(@Nullable FluidStack fluidStack, int capacity)
    {
        this.fluid = fluidStack;
        this.capacity = capacity;
    }

    public FluidTank(Fluid fluid, int amount, int capacity)
    {
        this(new FluidStack(fluid, amount), capacity);
    }

    public FluidTank readFromNBT(NBTTagCompound nbt)
    {
        if (!nbt.hasKey("Empty"))
        {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            setFluid(fluid);
        }
        else
        {
            setFluid(null);
        }
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        if (fluid != null)
        {
            fluid.writeToNBT(nbt);
        }
        else
        {
            nbt.setString("Empty", "");
        }
        return nbt;
    }

    /* IFluidTank */
    @Override
    @Nullable
    public FluidStack getFluid()
    {
        return fluid;
    }

    public void setFluid(@Nullable FluidStack fluid)
    {
        this.fluid = fluid;
    }

    @Override
    public int getFluidAmount()
    {
        if (fluid == null)
        {
            return 0;
        }
        return fluid.amount;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    public void setTileEntity(TileEntity tile)
    {
        this.tile = tile;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return new FluidTankInfo(this);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        if (this.tankProperties == null)
        {
            this.tankProperties = new IFluidTankProperties[] { new FluidTankPropertiesWrapper(this) };
        }
        return this.tankProperties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource == null || resource.amount <= 0 || !canFillFluidType(resource))
        {
            return 0;
        }

        if (!doFill)
        {
            if (fluid == null)
            {
                return Math.min(capacity, resource.amount);
            }

            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }

            return Math.min(capacity - fluid.amount, resource.amount);
        }

        if (fluid == null)
        {
            fluid = new FluidStack(resource, Math.min(capacity, resource.amount));

            onContentsChanged();

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorld(), tile.getPos(), this, fluid.amount));
            }
            return fluid.amount;
        }

        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacity - fluid.amount;

        if (resource.amount < filled)
        {
            fluid.amount += resource.amount;
            filled = resource.amount;
        }
        else
        {
            fluid.amount = capacity;
        }

        onContentsChanged();

        if (tile != null)
        {
            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluid, tile.getWorld(), tile.getPos(), this, filled));
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(getFluid()))
        {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (fluid == null || maxDrain <= 0 || !canDrainFluidType(fluid))
        {
            return null;
        }

        int drained = maxDrain;
        if (fluid.amount < drained)
        {
            drained = fluid.amount;
        }

        FluidStack stack = new FluidStack(fluid, drained);
        if (doDrain)
        {
            fluid.amount -= drained;
            if (fluid.amount <= 0)
            {
                fluid = null;
            }

            onContentsChanged();

            if (tile != null)
            {
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluid, tile.getWorld(), tile.getPos(), this, drained));
            }
        }
        return stack;
    }

    /**
     * Whether this tank can be filled with {@link IFluidHandler}
     *
     * @see IFluidTankProperties#canFill()
     */
    public boolean canFill()
    {
        return canFill;
    }

    /**
     * Whether this tank can be drained with {@link IFluidHandler}
     *
     * @see IFluidTankProperties#canDrain()
     */
    public boolean canDrain()
    {
        return canDrain;
    }

    /**
     * Set whether this tank can be filled with {@link IFluidHandler}
     *
     * @see IFluidTankProperties#canFill()
     */
    public void setCanFill(boolean canFill)
    {
        this.canFill = canFill;
    }

    /**
     * Set whether this tank can be drained with {@link IFluidHandler}
     *
     * @see IFluidTankProperties#canDrain()
     */
    public void setCanDrain(boolean canDrain)
    {
        this.canDrain = canDrain;
    }

    /**
     * Returns true if the tank can be filled with this type of fluid.
     * Used as a filter for fluid types.
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever fill with this type of fluid.
     *
     * @see IFluidTankProperties#canFillFluidType(FluidStack)
     */
    public boolean canFillFluidType(FluidStack fluid)
    {
        return canFill();
    }

    /**
     * Returns true if the tank can drain out this type of fluid.
     * Used as a filter for fluid types.
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever drain out this type of fluid.
     *
     * @see IFluidTankProperties#canDrainFluidType(FluidStack)
     */
    public boolean canDrainFluidType(FluidStack fluid)
    {
        return canDrain();
    }

    protected void onContentsChanged()
    {

    }
}
