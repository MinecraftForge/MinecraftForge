/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.CapabilityType;
import net.minecraftforge.common.capabilities.CapabilityTypes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileFluidHandler extends BlockEntity
{
    protected FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME);

    private final Capability<IFluidHandler> holder = Capability.of(() -> tank);

    public TileFluidHandler(@NotNull BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state)
    {
        super(blockEntityType, pos, state);
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        tank.readFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tank.writeToNBT(tag);
    }

    @Override
    @NotNull
    public <T> Capability<T> getCapability(@NotNull CapabilityType<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityTypes.FLUIDS)
            return holder.cast();
        return super.getCapability(capability, facing);
    }
}
