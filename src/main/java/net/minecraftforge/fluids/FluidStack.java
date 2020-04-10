/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fluids;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nullable;

/**
 * ItemStack substitute for Fluids.
 *
 * NOTE: Equality is based on the Fluid, not the amount. Use
 * {@link #isFluidStackIdentical(FluidStack)} to determine if FluidID, Amount and NBT Tag are all
 * equal.
 *
 */
public class FluidStack
{
    public int amount;
    public NBTTagCompound tag;
    private IRegistryDelegate<Fluid> fluidDelegate;

    public FluidStack(Fluid fluid, int amount)
    {
        if (fluid == null)
        {
            FMLLog.bigWarning("Null fluid supplied to fluidstack. Did you try and create a stack for an unregistered fluid?");
            throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
        }
        else if (!FluidRegistry.isFluidRegistered(fluid))
        {
            FMLLog.bigWarning("Failed attempt to create a FluidStack for an unregistered Fluid {} (type {})", fluid.getName(), fluid.getClass().getName());
            throw new IllegalArgumentException("Cannot create a fluidstack from an unregistered fluid");
        }
        this.fluidDelegate = FluidRegistry.makeDelegate(fluid);
        this.amount = amount;
    }

    public FluidStack(Fluid fluid, int amount, NBTTagCompound nbt)
    {
        this(fluid, amount);

        if (nbt != null)
        {
            tag = (NBTTagCompound) nbt.copy();
        }
    }

    public FluidStack(FluidStack stack, int amount)
    {
        this(stack.getFluid(), amount, stack.tag);
    }

    /**
     * This provides a safe method for retrieving a FluidStack - if the Fluid is invalid, the stack
     * will return as null.
     */
    @Nullable
    public static FluidStack loadFluidStackFromNBT(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return null;
        }
        if (!nbt.hasKey("FluidName", Constants.NBT.TAG_STRING))
        {
            return null;
        }

        String fluidName = nbt.getString("FluidName");
        if (FluidRegistry.getFluid(fluidName) == null)
        {
            return null;
        }
        FluidStack stack = new FluidStack(FluidRegistry.getFluid(fluidName), nbt.getInteger("Amount"));

        if (nbt.hasKey("Tag"))
        {
            stack.tag = nbt.getCompoundTag("Tag");
        }
        return stack;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setString("FluidName", FluidRegistry.getFluidName(getFluid()));
        nbt.setInteger("Amount", amount);

        if (tag != null)
        {
            nbt.setTag("Tag", tag);
        }
        return nbt;
    }

    public final Fluid getFluid()
    {
        return fluidDelegate.get();
    }

    public String getLocalizedName()
    {
        return this.getFluid().getLocalizedName(this);
    }

    public String getUnlocalizedName()
    {
        return this.getFluid().getUnlocalizedName(this);
    }

    /**
     * @return A copy of this FluidStack
     */
    public FluidStack copy()
    {
        return new FluidStack(getFluid(), amount, tag);
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal. This does not check amounts.
     *
     * @param other
     *            The FluidStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(@Nullable FluidStack other)
    {
        return other != null && getFluid() == other.getFluid() && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(FluidStack other)
    {
        return tag == null ? other.tag == null : other.tag == null ? false : tag.equals(other.tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
     */
    public static boolean areFluidStackTagsEqual(@Nullable FluidStack stack1, @Nullable FluidStack stack2)
    {
        return stack1 == null && stack2 == null ? true : stack1 == null || stack2 == null ? false : stack1.isFluidStackTagEqual(stack2);
    }

    /**
     * Determines if the Fluids are equal and this stack is larger.
     *
     * @param other
     * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
     */
    public boolean containsFluid(@Nullable FluidStack other)
    {
        return isFluidEqual(other) && amount >= other.amount;
    }

    /**
     * Determines if the FluidIDs, Amounts, and NBT Tags are all equal.
     *
     * @param other
     *            - the FluidStack for comparison
     * @return true if the two FluidStacks are exactly the same
     */
    public boolean isFluidStackIdentical(FluidStack other)
    {
        return isFluidEqual(other) && amount == other.amount;
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal compared to a registered container
     * ItemStack. This does not check amounts.
     *
     * @param other
     *            The ItemStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(ItemStack other)
    {
        if (other == null)
        {
            return false;
        }

        return isFluidEqual(FluidUtil.getFluidContained(other));
    }

    @Override
    public final int hashCode()
    {
        int code = 1;
        code = 31*code + getFluid().hashCode();
        if (tag != null)
            code = 31*code + tag.hashCode();
        return code;
    }

    /**
     * Default equality comparison for a FluidStack. Same functionality as isFluidEqual().
     *
     * This is included for use in data structures.
     */
    @Override
    public final boolean equals(Object o)
    {
        if (!(o instanceof FluidStack))
        {
            return false;
        }

        return isFluidEqual((FluidStack) o);
    }
}
