/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fluids.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftsingularity.common.capabilities.Capability;
import net.minecraftsingularity.common.capabilities.singularityCapabilities;
import net.minecraftsingularity.common.util.LazyOptional;
import net.minecraftsingularity.fluids.FluidType;
import net.minecraftsingularity.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidHandlerBlockEntity extends BlockEntity {
    protected FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME);

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    public FluidHandlerBlockEntity(@NotNull BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tank.readFrom(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tank.writeTo(output);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == singularityCapabilities.FLUID_HANDLER)
            return holder.cast();
        return super.getCapability(capability, facing);
    }
}
