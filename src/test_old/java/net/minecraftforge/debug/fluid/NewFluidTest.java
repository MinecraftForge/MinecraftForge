/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.fluid;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftsingularity.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.fluids.DispenseFluidContainer;
import net.minecraftsingularity.fluids.FluidType;
import org.apache.commons.lang3.Validate;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.fluids.FluidStack;
import net.minecraftsingularity.fluids.singularityFlowingFluid;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Mod(NewFluidTest.MODID)
public class NewFluidTest {
    public static final boolean ENABLE = false; // TODO fix
    public static final String MODID = "new_fluid_test";

    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/brown_mushroom_block");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/mushroom_stem");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation("minecraft:block/obsidian");

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(singularityRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(singularityRegistries.Keys.FLUID_TYPES, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(singularityRegistries.FLUIDS, MODID);

    private static singularityFlowingFluid.Properties makeProperties() {
        return new singularityFlowingFluid.Properties(test_fluid_type, test_fluid, test_fluid_flowing)
                .bucket(TEST_FLUID_BUCKET).block(test_fluid_block);
    }

    public static RegistryObject<FluidType> test_fluid_type = FLUID_TYPES.register("test_fluid", () -> new FluidType(FluidType.Properties.create()) {
        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return FLUID_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLUID_FLOWING;
                }

                @Nullable
                @Override
                public ResourceLocation getOverlayTexture() {
                    return FLUID_OVERLAY;
                }

                @Override
                public int getTintColor() {
                    return 0x3F1080FF;
                }
            });
        }
    });

    public static RegistryObject<FlowingFluid> test_fluid = FLUIDS.register("test_fluid", () ->
            new singularityFlowingFluid.Source(makeProperties())
    );
    public static RegistryObject<FlowingFluid> test_fluid_flowing = FLUIDS.register("test_fluid_flowing", () ->
            new singularityFlowingFluid.Flowing(makeProperties())
    );

    public static RegistryObject<LiquidBlock> test_fluid_block = BLOCKS.register("test_fluid_block", () ->
            new LiquidBlock(test_fluid, Properties.of().noCollission().strength(100.0F).noLootTable())
    );
    public static RegistryObject<Item> TEST_FLUID_BUCKET = ITEMS.register("test_fluid_bucket", () ->
            new BucketItem(test_fluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))
    );

    // WARNING: this doesn't allow "any fluid", only the fluid from this test mod!
    public static RegistryObject<Block> fluidloggable_block = BLOCKS.register("fluidloggable_block", () ->
            new FluidloggableBlock(Properties.of().mapColor(MapColor.WOOD).noCollission().strength(100.0F).noLootTable())
    );
    public static RegistryObject<Item> FLUID_LOGGABLE_BLOCK_ITEM = ITEMS.register("fluidloggable_block", () ->
            new BlockItem(fluidloggable_block.get(), new Item.Properties())
    );

    public NewFluidTest() {
        if (ENABLE) {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

            modEventBus.addListener(this::loadComplete);

            BLOCKS.register(modEventBus);
            ITEMS.register(modEventBus);
            FLUID_TYPES.register(modEventBus);
            FLUIDS.register(modEventBus);
            modEventBus.addListener(this::addCreative);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(FLUID_LOGGABLE_BLOCK_ITEM);
            event.accept(TEST_FLUID_BUCKET);
        }
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        // some sanity checks
        BlockState state = Fluids.WATER.defaultFluidState().createLegacyBlock();
        BlockState state2 = Fluids.WATER.getFluidType().getBlockForFluidState(null,null,Fluids.WATER.defaultFluidState());
        Validate.isTrue(state.getBlock() == Blocks.WATER && state2 == state);
        ItemStack stack = Fluids.WATER.getFluidType().getBucket(new FluidStack(Fluids.WATER, 1));
        Validate.isTrue(stack.getItem() == Fluids.WATER.getBucket());
        event.enqueueWork(() -> DispenserBlock.registerBehavior(TEST_FLUID_BUCKET.get(), DispenseFluidContainer.getInstance()));
    }

    // WARNING: this doesn't allow "any fluid", only the fluid from this test mod!
    private static class FluidloggableBlock extends Block implements SimpleWaterloggedBlock {
        public static final BooleanProperty FLUIDLOGGED = BooleanProperty.create("fluidlogged");

        public FluidloggableBlock(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(FLUIDLOGGED, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(FLUIDLOGGED);
        }

        @Override
        public boolean canPlaceLiquid(@Nullable Player player, BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
            return !state.getValue(FLUIDLOGGED) && fluidIn == test_fluid.get();
        }

        @Override
        public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
            if (canPlaceLiquid(null, worldIn, pos, state, fluidStateIn.getType())) {
                if (!worldIn.isClientSide()) {
                    worldIn.setBlock(pos, state.setValue(FLUIDLOGGED, true), 3);
                    worldIn.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public ItemStack pickupBlock(@Nullable Player player, LevelAccessor worldIn, BlockPos pos, BlockState state) {
            if (state.getValue(FLUIDLOGGED)) {
                worldIn.setBlock(pos, state.setValue(FLUIDLOGGED, false), 3);
                return new ItemStack(TEST_FLUID_BUCKET.get());
            } else
                return ItemStack.EMPTY;
        }

        @Override
        public FluidState getFluidState(BlockState state) {
            return state.getValue(FLUIDLOGGED) ? test_fluid.get().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
        }
    }
}
