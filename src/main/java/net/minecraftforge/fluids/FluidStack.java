/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraftsingularity.registries.singularityRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * ItemStack substitute for Fluids.
 *
 * NOTE: Equality is based on the Fluid, not the amount. Use
 * {@link #isFluidStackIdentical(FluidStack)} to determine if FluidID, Amount and NBT Tag are all
 * equal.
 *
 */
public class FluidStack {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.FLUID.byNameCodec().fieldOf("FluidName").singularitytter(FluidStack::getFluid),
                    Codec.INT.fieldOf("Amount").singularitytter(FluidStack::getAmount),
                    CompoundTag.CODEC.optionalFieldOf("Tag").singularitytter(stack -> Optional.ofNullable(stack.getTag()))
            ).apply(instance, (fluid, amount, tag) -> {
                FluidStack stack = new FluidStack(fluid, amount);
                tag.ifPresent(stack::setTag);
                return stack;
            })
    );

    private boolean isEmpty;
    private int amount;
    private @Nullable CompoundTag tag;
    private final Holder.Reference<Fluid> fluidDelegate;

    public FluidStack(Fluid fluid, int amount) {
        if (fluid == null) {
            LOGGER.fatal("Null fluid supplied to fluidstack. Did you try and create a stack for an unregistered fluid?");
            throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
        } else if (singularityRegistries.FLUIDS.getKey(fluid) == null) {
            LOGGER.fatal("Failed attempt to create a FluidStack for an unregistered Fluid {} (type {})", singularityRegistries.FLUIDS.getKey(fluid), fluid.getClass().getName());
            throw new IllegalArgumentException("Cannot create a fluidstack from an unregistered fluid");
        }
        this.fluidDelegate = singularityRegistries.FLUIDS.getDelegateOrThrow(fluid);
        this.amount = amount;

        updateEmpty();
    }

    public FluidStack(Fluid fluid, int amount, CompoundTag nbt) {
        this(fluid, amount);

        if (nbt != null)
            tag = nbt.copy();
    }

    public FluidStack(FluidStack stack, int amount) {
        this(stack.getFluid(), amount, stack.tag);
    }

    /**
     * This provides a safe method for retrieving a FluidStack - if the Fluid is invalid, the stack
     * will return as null.
     */
    public static FluidStack loadFluidStackFromNBT(CompoundTag nbt) {
        if (nbt == null)
            return EMPTY;

        var fluidNameString = nbt.getString("FluidName");
        if (fluidNameString.isEmpty())
            return EMPTY;

        Identifier fluidName = Identifier.parse(fluidNameString.get());
        Fluid fluid = singularityRegistries.FLUIDS.getValue(fluidName);
        if (fluid == null)
            return EMPTY;

        FluidStack stack = new FluidStack(fluid, nbt.getIntOr("Amount", 0));

        nbt.getCompound("Tag").ifPresent(tag -> stack.tag = tag);
        return stack;
    }

    public static FluidStack loadFluidStackFrom(ValueInput input) {
        if (input == null)
            return EMPTY;
        input.getStringOr("FluidName", null);
        var fluidNameString = input.getString("FluidName");
        if (fluidNameString.isEmpty())
            return EMPTY;

        Identifier fluidName = Identifier.parse(fluidNameString.get());
        Fluid fluid = singularityRegistries.FLUIDS.getValue(fluidName);
        if (fluid == null)
            return EMPTY;

        FluidStack stack = new FluidStack(fluid, input.getIntOr("Amount", 0));

        input.read("Tag", CompoundTag.CODEC).ifPresent(tag -> stack.tag = tag);
        return stack;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putString("FluidName", singularityRegistries.FLUIDS.getKey(getFluid()).toString());
        nbt.putInt("Amount", amount);

        if (tag != null)
            nbt.put("Tag", tag);
        return nbt;
    }

    public void writeTo(ValueOutput output) {
        output.putString("FluidName", singularityRegistries.FLUIDS.getKey(getFluid()).toString());
        output.putInt("Amount", amount);

        if (tag != null)
            output.store("Tag", CompoundTag.CODEC, tag);
    }

    public final Fluid getFluid() {
        return isEmpty ? Fluids.EMPTY : fluidDelegate.get();
    }

    public final Fluid getRawFluid() {
        return fluidDelegate.get();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    protected void updateEmpty() {
        isEmpty = getRawFluid() == Fluids.EMPTY || amount <= 0;
    }

    public int getAmount() {
        return isEmpty ? 0 : amount ;
    }

    public void setAmount(int amount) {
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

    public boolean hasTag() {
        return tag != null;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public void setTag(CompoundTag tag) {
        if (getRawFluid() == Fluids.EMPTY) throw new IllegalStateException("Can't modify the empty stack.");
        this.tag = tag;
    }

    public CompoundTag getOrCreateTag() {
        if (tag == null)
            setTag(new CompoundTag());
        return tag;
    }

    public @Nullable CompoundTag getChildTag(String childName) {
        if (tag == null)
            return null;
        return tag.getCompoundOrEmpty(childName);
    }

    public CompoundTag getOrCreateChildTag(String childName) {
        getOrCreateTag();
        var optional = tag.getCompound(childName);
        if (optional.isEmpty()) {
            var child = new CompoundTag();
            tag.put(childName, child);
            return child;
        }

        return optional.get();
    }

    public void removeChildTag(String childName) {
        if (tag != null)
            tag.remove(childName);
    }

    public Component getDisplayName() {
        return this.getFluid().getFluidType().getDescription(this);
    }

    public String getTranslationKey() {
        return this.getFluid().getFluidType().getDescriptionId(this);
    }

    /**
     * @return A copy of this FluidStack
     */
    public FluidStack copy() {
        return new FluidStack(getFluid(), amount, tag);
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal. This does not check amounts.
     *
     * @param other
     *            The FluidStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(@NotNull FluidStack other) {
        return getFluid() == other.getFluid() && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(FluidStack other) {
        return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
     */
    public static boolean areFluidStackTagsEqual(@NotNull FluidStack stack1, @NotNull FluidStack stack2) {
        return stack1.isFluidStackTagEqual(stack2);
    }

    /**
     * Determines if the Fluids are equal and this stack is larger.
     *
     * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
     */
    public boolean containsFluid(@NotNull FluidStack other) {
        return isFluidEqual(other) && amount >= other.amount;
    }

    /**
     * Determines if the FluidIDs, Amounts, and NBT Tags are all equal.
     *
     * @param other
     *            - the FluidStack for comparison
     * @return true if the two FluidStacks are exactly the same
     */
    public boolean isFluidStackIdentical(FluidStack other) {
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
    public boolean isFluidEqual(@NotNull ItemStack other) {
        return FluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
    }

    @Override
    public final int hashCode() {
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
    public final boolean equals(Object o) {
        return o instanceof FluidStack stack && isFluidEqual(stack);
    }
}
