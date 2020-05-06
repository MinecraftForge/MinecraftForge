/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

@Mod(NewFluidTest.MODID)
public class NewFluidTest
{
    public static final String MODID = "new_fluid_test";

    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/brown_mushroom_block");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/mushroom_stem");

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, MODID);

    private static ForgeFlowingFluid.Properties makeProperties()
    {
        return new ForgeFlowingFluid.Properties(test_fluid, test_fluid_flowing,
                FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).color(0x3F1080FF))
                .bucket(test_fluid_bucket).block(test_fluid_block);
    }

    public static RegistryObject<FlowingFluid> test_fluid = FLUIDS.register("test_fluid", () ->
            new ForgeFlowingFluid.Source(makeProperties())
    );
    public static RegistryObject<FlowingFluid> test_fluid_flowing = FLUIDS.register("test_fluid_flowing", () ->
            new ForgeFlowingFluid.Flowing(makeProperties())
    );

    public static RegistryObject<FlowingFluidBlock> test_fluid_block = BLOCKS.register("test_fluid_block", () ->
            new FlowingFluidBlock(test_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
    );
    public static RegistryObject<Item> test_fluid_bucket = ITEMS.register("test_fluid_bucket", () ->
            new BucketItem(test_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC))
    );

    // WARNING: this doesn't allow "any fluid", only the fluid from this test mod!
    public static RegistryObject<Block> fluidloggable_block = BLOCKS.register("fluidloggable_block", () ->
            new FluidloggableBlock(Block.Properties.create(Material.WOOD).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
    );
    public static RegistryObject<Item> fluidloggable_blockitem = ITEMS.register("fluidloggable_block", () ->
            new BlockItem(fluidloggable_block.get(), new Item.Properties().group(ItemGroup.MISC))
    );

    public NewFluidTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::loadComplete);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        FLUIDS.register(modEventBus);
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        // some sanity checks
        BlockState state = Fluids.WATER.getDefaultState().getBlockState();
        BlockState state2 = Fluids.WATER.getAttributes().getBlock(null,null,Fluids.WATER.getDefaultState());
        Validate.isTrue(state.getBlock() == Blocks.WATER && state2 == state);
        ItemStack stack = Fluids.WATER.getAttributes().getBucket(new FluidStack(Fluids.WATER, 1));
        Validate.isTrue(stack.getItem() == Fluids.WATER.getFilledBucket());
    }

    // WARNING: this doesn't allow "any fluid", only the fluid from this test mod!
    private static class FluidloggableBlock extends Block implements IWaterLoggable
    {
        public static final BooleanProperty FLUIDLOGGED = BooleanProperty.create("fluidlogged");

        public FluidloggableBlock(Properties properties)
        {
            super(properties);
            setDefaultState(getStateContainer().getBaseState().with(FLUIDLOGGED, false));
        }

        @Override
        protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
        {
            builder.add(FLUIDLOGGED);
        }

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockItemUseContext context)
        {
            IFluidState state = context.getWorld().getFluidState(context.getPos());
            return getDefaultState().with(FLUIDLOGGED, state.getFluid() == test_fluid.get());
        }

        public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
            if (stateIn.get(FLUIDLOGGED)) {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, test_fluid.get(), test_fluid.get().getTickRate(worldIn));
            }

            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }

        @Override
        public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
            return !state.get(FLUIDLOGGED) && fluidIn == test_fluid.get();
        }

        @Override
        public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
            if (canContainFluid(worldIn, pos, state, fluidStateIn.getFluid())) {
                if (!worldIn.isRemote()) {
                    worldIn.setBlockState(pos, state.with(FLUIDLOGGED, true), 3);
                    worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
            if (state.get(FLUIDLOGGED)) {
                worldIn.setBlockState(pos, state.with(FLUIDLOGGED, false), 3);
                return test_fluid.get();
            } else {
                return Fluids.EMPTY;
            }
        }

        @Override
        public IFluidState getFluidState(BlockState state)
        {
            return state.get(FLUIDLOGGED) ? test_fluid.get().getDefaultState() : Fluids.EMPTY.getDefaultState();
        }
    }
}
