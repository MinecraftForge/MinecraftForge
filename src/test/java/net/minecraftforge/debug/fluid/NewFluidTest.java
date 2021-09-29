/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.fluid;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fluids.DispenseFluidContainer;
import org.apache.commons.lang3.Validate;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mod(NewFluidTest.MODID)
public class NewFluidTest
{
    public static final boolean ENABLE = false; // TODO fix
    public static final String MODID = "new_fluid_test";

    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/brown_mushroom_block");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/mushroom_stem");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation("minecraft:block/obsidian");

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

    private static ForgeFlowingFluid.Properties makeProperties()
    {
        return new ForgeFlowingFluid.Properties(test_fluid, test_fluid_flowing,
                FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).overlay(FLUID_OVERLAY).color(0x3F1080FF))
                .bucket(test_fluid_bucket).block(test_fluid_block);
    }

    public static RegistryObject<FlowingFluid> test_fluid = FLUIDS.register("test_fluid", () ->
            new ForgeFlowingFluid.Source(makeProperties())
    );
    public static RegistryObject<FlowingFluid> test_fluid_flowing = FLUIDS.register("test_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeProperties())
    );

    public static RegistryObject<LiquidBlock> test_fluid_block = BLOCKS.register("test_fluid_block", () ->
            new LiquidBlock(test_fluid, Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops())
    );
    public static RegistryObject<Item> test_fluid_bucket = ITEMS.register("test_fluid_bucket", () ->
            new BucketItem(test_fluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC))
    );

    // WARNING: this doesn't allow "any fluid", only the fluid from this test mod!
    public static RegistryObject<Block> fluidloggable_block = BLOCKS.register("fluidloggable_block", () ->
            new FluidloggableBlock(Properties.of(Material.WOOD).noCollission().strength(100.0F).noDrops())
    );
    public static RegistryObject<Item> fluidloggable_blockitem = ITEMS.register("fluidloggable_block", () ->
            new BlockItem(fluidloggable_block.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public NewFluidTest()
    {
        if (ENABLE)
        {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

            modEventBus.addListener(this::loadComplete);

            BLOCKS.register(modEventBus);
            ITEMS.register(modEventBus);
            FLUIDS.register(modEventBus);
        }
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        // some sanity checks
        BlockState state = Fluids.WATER.defaultFluidState().createLegacyBlock();
        BlockState state2 = Fluids.WATER.getAttributes().getBlock(null,null,Fluids.WATER.defaultFluidState());
        Validate.isTrue(state.getBlock() == Blocks.WATER && state2 == state);
        ItemStack stack = Fluids.WATER.getAttributes().getBucket(new FluidStack(Fluids.WATER, 1));
        Validate.isTrue(stack.getItem() == Fluids.WATER.getBucket());
        event.enqueueWork(() -> DispenserBlock.registerBehavior(test_fluid_bucket.get(), DispenseFluidContainer.getInstance()));
    }

    // WARNING: this doesn't allow "any fluid", only the fluid from this test mod!
    private static class FluidloggableBlock extends Block implements SimpleWaterloggedBlock
    {
        public static final BooleanProperty FLUIDLOGGED = BooleanProperty.create("fluidlogged");

        public FluidloggableBlock(Properties properties)
        {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(FLUIDLOGGED, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
        {
            builder.add(FLUIDLOGGED);
        }

        @Override
        public boolean canPlaceLiquid(BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
            return !state.getValue(FLUIDLOGGED) && fluidIn == test_fluid.get();
        }

        @Override
        public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
            if (canPlaceLiquid(worldIn, pos, state, fluidStateIn.getType())) {
                if (!worldIn.isClientSide()) {
                    worldIn.setBlock(pos, state.setValue(FLUIDLOGGED, true), 3);
                    worldIn.getLiquidTicks().scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public ItemStack pickupBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
            if (state.getValue(FLUIDLOGGED)) {
                worldIn.setBlock(pos, state.setValue(FLUIDLOGGED, false), 3);
                return new ItemStack(test_fluid_bucket.get());
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public FluidState getFluidState(BlockState state)
        {
            return state.getValue(FLUIDLOGGED) ? test_fluid.get().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
        }
    }
}
