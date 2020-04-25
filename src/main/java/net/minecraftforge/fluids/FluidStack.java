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

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger();

    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private boolean isEmpty;
    private int amount;
    private CompoundNBT tag;
    private IRegistryDelegate<Fluid> fluidDelegate;

    public FluidStack(Fluid fluid, int amount)
    {
        if (fluid == null)
        {
            LOGGER.fatal("Null fluid supplied to fluidstack. Did you try and create a stack for an unregistered fluid?");
            throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
        }
        else if (ForgeRegistries.FLUIDS.getKey(fluid) == null)
        {
            LOGGER.fatal("Failed attempt to create a FluidStack for an unregistered Fluid {} (type {})", fluid.getRegistryName(), fluid.getClass().getName());
            throw new IllegalArgumentException("Cannot create a fluidstack from an unregistered fluid");
        }
        this.fluidDelegate = fluid.delegate;
        this.amount = amount;

        updateEmpty();
    }

    public FluidStack(Fluid fluid, int amount, CompoundNBT nbt)
    {
        this(fluid, amount);

        if (nbt != null)
        {
            tag = nbt.copy();
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
    public static FluidStack loadFluidStackFromNBT(CompoundNBT nbt)
    {
        if (nbt == null)
        {
            return EMPTY;
        }
        if (!nbt.contains("FluidName", Constants.NBT.TAG_STRING))
        {
            return EMPTY;
        }

        ResourceLocation fluidName = new ResourceLocation(nbt.getString("FluidName"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
        if (fluid == null)
        {
            return EMPTY;
        }
        FluidStack stack = new FluidStack(fluid, nbt.getInt("Amount"));

        if (nbt.contains("Tag", Constants.NBT.TAG_COMPOUND))
        {
            stack.tag = nbt.getCompound("Tag");
        }
        return stack;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt)
    {
        nbt.putString("FluidName", getFluid().getRegistryName().toString());
        nbt.putInt("Amount", amount);

        if (tag != null)
        {
            nbt.put("Tag", tag);
        }
        return nbt;
    }

    public void writeToPacket(PacketBuffer buf)
    {
        buf.writeRegistryId(getFluid());
        buf.writeVarInt(getAmount());
        buf.writeCompoundTag(tag);
    }

    public static FluidStack readFromPacket(PacketBuffer buf)
    {
        Fluid fluid = buf.readRegistryId();
        int amount = buf.readVarInt();
        CompoundNBT tag = buf.readCompoundTag();
        if (fluid == Fluids.EMPTY) return EMPTY;
        return new FluidStack(fluid, amount, tag);
    }

    public final Fluid getFluid()
    {
        return isEmpty ? Fluids.EMPTY : fluidDelegate.get();
    }

    public final Fluid getRawFluid()
    {
        return fluidDelegate.get();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    protected void updateEmpty() {
        isEmpty = getRawFluid() == Fluids.EMPTY || amount <= 0;
    }

    public int getAmount()
    {
        return isEmpty ? 0 : amount ;
    }

    public void setAmount(int amount)
    {
        if (getRawFluid() == Fluids.EMPTY) throw new IllegalStateException("Can't modify the empty stack.");
        this.amount = amount;
        updateEmpty();
    }

    public void grow(int amount) {
        setAmount(this.amount + amount);
    }

    public void shrink(int amount) {
        setAmount(this.amount - amount);
    }

    public boolean hasTag()
    {
        return tag != null;
    }

    public CompoundNBT getTag()
    {
        return tag;
    }

    public void setTag(CompoundNBT tag)
    {
        if (getRawFluid() == Fluids.EMPTY) throw new IllegalStateException("Can't modify the empty stack.");
        this.tag = tag;
    }

    public CompoundNBT getOrCreateTag()
    {
        if (tag == null)
            setTag(new CompoundNBT());
        return tag;
    }

    public CompoundNBT getChildTag(String childName)
    {
        if (tag == null)
            return null;
        return tag.getCompound(childName);
    }

    public CompoundNBT getOrCreateChildTag(String childName)
    {
        getOrCreateTag();
        CompoundNBT child = tag.getCompound(childName);
        if (!tag.contains(childName, Constants.NBT.TAG_COMPOUND))
        {
            tag.put(childName, child);
        }
        return child;
    }

    public void removeChildTag(String childName)
    {
        if (tag != null)
            tag.remove(childName);
    }

    public ITextComponent getDisplayName()
    {
        return this.getFluid().getAttributes().getDisplayName(this);
    }

    public String getTranslationKey()
    {
        return this.getFluid().getAttributes().getTranslationKey(this);
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
    public boolean isFluidEqual(@Nonnull FluidStack other)
    {
        return getFluid() == other.getFluid() && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(FluidStack other)
    {
        return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
     */
    public static boolean areFluidStackTagsEqual(@Nonnull FluidStack stack1, @Nonnull FluidStack stack2)
    {
        return stack1.isFluidStackTagEqual(stack2);
    }

    /**
     * Determines if the Fluids are equal and this stack is larger.
     *
     * @param other
     * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
     */
    public boolean containsFluid(@Nonnull FluidStack other)
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
    public boolean isFluidEqual(@Nonnull ItemStack other)
    {
        return FluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
    }

    @Override
    public final int hashCode()
    {
        int code = 1;
        code = 31*code + getFluid().hashCode();
        code = 31*code + amount;
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
