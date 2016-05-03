package net.minecraftforge.fluids.capability.templates;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * FluidTankItemStack is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old deprecated {@link ItemFluidContainer}.
 */
public class FluidTankItemStack implements IFluidTank, IFluidHandler, ICapabilityProvider
{
    protected final ItemStack stack;
    protected int capacity;

    public FluidTankItemStack(ItemStack stack, int capacity)
    {
        super();
        this.stack = stack;
        this.capacity = capacity;
    }

    @Override
    public FluidStack getFluid()
    {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Fluid"))
        {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("Fluid"));
    }

    @Override
    public int getFluidAmount()
    {
        FluidStack fluid = getFluid();
        return fluid != null ? fluid.amount : 0;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public FluidTankInfo getInfo()
    {
        return new FluidTankInfo(this);
    }

    @Override
    public FluidTankInfo[] getTankInfo()
    {
        return new FluidTankInfo[] { getInfo() };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (resource == null || !canFill(resource.getFluid()))
        {
            return 0;
        }

        if (!doFill)
        {
            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Fluid"))
            {
                return Math.min(capacity, resource.amount);
            }

            FluidStack stack = FluidStack.loadFluidStackFromNBT(this.stack.getTagCompound().getCompoundTag("Fluid"));

            if (stack == null)
            {
                return Math.min(capacity, resource.amount);
            }

            if (!stack.isFluidEqual(resource))
            {
                return 0;
            }

            return Math.min(capacity - stack.amount, resource.amount);
        }

        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("Fluid"))
        {
            NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());

            if (capacity < resource.amount)
            {
                fluidTag.setInteger("Amount", capacity);
                stack.getTagCompound().setTag("Fluid", fluidTag);
                return capacity;
            }

            stack.getTagCompound().setTag("Fluid", fluidTag);
            return resource.amount;
        }

        NBTTagCompound fluidTag = stack.getTagCompound().getCompoundTag("Fluid");
        FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidTag);

        if (!stack.isFluidEqual(resource))
        {
            return 0;
        }

        int filled = capacity - stack.amount;
        if (resource.amount < filled)
        {
            stack.amount += resource.amount;
            filled = resource.amount;
        }
        else
        {
            stack.amount = capacity;
        }

        this.stack.getTagCompound().setTag("Fluid", stack.writeToNBT(fluidTag));
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
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Fluid"))
        {
            return null;
        }

        FluidStack stack = FluidStack.loadFluidStackFromNBT(this.stack.getTagCompound().getCompoundTag("Fluid"));
        if (stack == null || canDrain(stack.getFluid()))
        {
            return null;
        }

        int currentAmount = stack.amount;
        stack.amount = Math.min(stack.amount, maxDrain);
        if (doDrain)
        {
            if (currentAmount == stack.amount)
            {
                this.stack.getTagCompound().removeTag("Fluid");

                if (this.stack.getTagCompound().hasNoTags())
                {
                    this.stack.setTagCompound(null);
                }
                return stack;
            }

            NBTTagCompound fluidTag = this.stack.getTagCompound().getCompoundTag("Fluid");
            fluidTag.setInteger("Amount", currentAmount - stack.amount);
            this.stack.getTagCompound().setTag("Fluid", fluidTag);
        }
        return stack;
    }

    public boolean canFill(Fluid fluid)
    {
        return true;
    }

    public boolean canDrain(Fluid fluid)
    {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
    }

}
