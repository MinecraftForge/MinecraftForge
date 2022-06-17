/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids;

import java.util.Optional;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.SoundActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class ForgeFlowingFluid extends FlowingFluid
{
    private final Supplier<? extends FluidType> fluidType;
    private final Supplier<? extends Fluid> flowing;
    private final Supplier<? extends Fluid> still;
    @Nullable
    private final Supplier<? extends Item> bucket;
    @Nullable
    private final Supplier<? extends LiquidBlock> block;
    private final int slopeFindDistance;
    private final int levelDecreasePerBlock;
    private final float explosionResistance;
    private final int tickRate;

    protected ForgeFlowingFluid(Properties properties)
    {
        this.fluidType = properties.fluidType;
        this.flowing = properties.flowing;
        this.still = properties.still;
        this.bucket = properties.bucket;
        this.block = properties.block;
        this.slopeFindDistance = properties.slopeFindDistance;
        this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
        this.explosionResistance = properties.explosionResistance;
        this.tickRate = properties.tickRate;
    }

    @Override
    public FluidType getFluidType()
    {
        return this.fluidType.get();
    }

    @Override
    public Fluid getFlowing()
    {
        return flowing.get();
    }

    @Override
    public Fluid getSource()
    {
        return still.get();
    }

    @Override
    protected boolean canConvertToSource()
    {
        return false;
    }

    @Override
    public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos)
    {
        return this.getFluidType().canConvertToSource(state, reader, pos);
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor worldIn, BlockPos pos, BlockState state)
    {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        Block.dropResources(state, worldIn, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader worldIn)
    {
        return slopeFindDistance;
    }

    @Override
    protected int getDropOff(LevelReader worldIn)
    {
        return levelDecreasePerBlock;
    }

    @Override
    public Item getBucket()
    {
        return bucket != null ? bucket.get() : Items.AIR;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction)
    {
        // Based on the water implementation, may need to be overriden for mod fluids that shouldn't behave like water.
        return direction == Direction.DOWN && !isSame(fluidIn);
    }

    @Override
    public int getTickDelay(LevelReader level)
    {
        return tickRate;
    }

    @Override
    protected float getExplosionResistance()
    {
        return explosionResistance;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state)
    {
        if (block != null)
            return block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSame(Fluid fluidIn) {
        return fluidIn == still.get() || fluidIn == flowing.get();
    }

    @NotNull
    @Override
    public Optional<SoundEvent> getPickupSound()
    {
        return Optional.ofNullable(getFluidType().getSound(SoundActions.BUCKET_FILL));
    }

    public static class Flowing extends ForgeFlowingFluid
    {
        public Flowing(Properties properties)
        {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends ForgeFlowingFluid
    {
        public Source(Properties properties)
        {
            super(properties);
        }

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Properties
    {
        private Supplier<? extends FluidType> fluidType;
        private Supplier<? extends Fluid> still;
        private Supplier<? extends Fluid> flowing;
        private Supplier<? extends Item> bucket;
        private Supplier<? extends LiquidBlock> block;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1;
        private int tickRate = 5;

        public Properties(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing)
        {
            this.fluidType = fluidType;
            this.still = still;
            this.flowing = flowing;
        }

        public Properties bucket(Supplier<? extends Item> bucket)
        {
            this.bucket = bucket;
            return this;
        }

        public Properties block(Supplier<? extends LiquidBlock> block)
        {
            this.block = block;
            return this;
        }

        public Properties slopeFindDistance(int slopeFindDistance)
        {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        public Properties levelDecreasePerBlock(int levelDecreasePerBlock)
        {
            this.levelDecreasePerBlock = levelDecreasePerBlock;
            return this;
        }

        public Properties explosionResistance(float explosionResistance)
        {
            this.explosionResistance = explosionResistance;
            return this;
        }

        public Properties tickRate(int tickRate)
        {
            this.tickRate = tickRate;
            return this;
        }
    }
}
