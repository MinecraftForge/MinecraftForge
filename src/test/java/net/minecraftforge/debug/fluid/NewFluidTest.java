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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.Validate;

@Mod("new_fluid_test")
public class NewFluidTest
{
    public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/brown_mushroom_block");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/mushroom_stem");

    @ObjectHolder("forge:test_fluid")
    public static FlowingFluid test_fluid;

    @ObjectHolder("forge:test_fluid_flowing")
    public static Fluid test_fluid_flowing;

    @ObjectHolder("forge:test_fluid_bucket")
    public static Item test_fluid_bucket;

    @ObjectHolder("forge:test_fluid_block")
    public static Block test_fluid_block;

    public NewFluidTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(Item.class, this::registerItems);
        modEventBus.addGenericListener(Fluid.class, this::registerFluids);
        modEventBus.addListener(this::loadComplete);
    }

    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        (test_fluid = new MyFlowingFluid.Source()).setRegistryName("forge:test_fluid");
        test_fluid_flowing = new MyFlowingFluid.Flowing().setRegistryName("forge:test_fluid_flowing");

        event.getRegistry().registerAll(
                new FlowingFluidBlock(test_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
                {}
                .setRegistryName("forge:test_fluid_block")
        );
    }

    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                new BucketItem(test_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)).setRegistryName("forge:test_fluid_bucket")
        );
    }

    public void registerFluids(RegistryEvent.Register<Fluid> event)
    {
        event.getRegistry().registerAll(test_fluid, test_fluid_flowing);
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

    private static final FluidAttributes ATTRIBUTES = FluidAttributes.builder("test_fluid", FLUID_STILL, FLUID_FLOWING).build();

    private static abstract class MyFlowingFluid extends FlowingFluid
    {
        @Override
        public Fluid getFlowingFluid()
        {
            return test_fluid_flowing;
        }

        @Override
        public Fluid getStillFluid()
        {
            return test_fluid;
        }

        @Override
        protected boolean canSourcesMultiply()
        {
            return false;
        }

        @Override
        protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state)
        {
            // copied from the WaterFluid implementation
            TileEntity tileentity = state.getBlock().hasTileEntity() ? worldIn.getTileEntity(pos) : null;
            Block.spawnDrops(state, worldIn.getWorld(), pos, tileentity);
        }

        @Override
        protected int getSlopeFindDistance(IWorldReader worldIn)
        {
            return 4;
        }

        @Override
        protected int getLevelDecreasePerBlock(IWorldReader worldIn)
        {
            return 1;
        }

        @Override
        public BlockRenderLayer getRenderLayer()
        {
            return BlockRenderLayer.TRANSLUCENT;
        }

        @Override
        public Item getFilledBucket()
        {
            return test_fluid_bucket;
        }

        @Override
        protected boolean func_215665_a(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_)
        {
            return p_215665_5_ == Direction.DOWN && !p_215665_4_.isIn(FluidTags.WATER);
        }

        @Override
        public int getTickRate(IWorldReader p_205569_1_)
        {
            return 5;
        }

        @Override
        protected float getExplosionResistance()
        {
            return 1;
        }

        @Override
        protected BlockState getBlockState(IFluidState state)
        {
            return test_fluid_block.getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
        }

        @Override
        public boolean isEquivalentTo(Fluid fluidIn) {
            return fluidIn == test_fluid || fluidIn == test_fluid_flowing;
        }

        @Override
        protected FluidAttributes createAttributes(Fluid fluid)
        {
            return ATTRIBUTES;
        }

        public static class Flowing extends MyFlowingFluid
        {
            {
                setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
            }

            protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
                super.fillStateContainer(builder);
                builder.add(LEVEL_1_8);
            }

            public int getLevel(IFluidState p_207192_1_) {
                return p_207192_1_.get(LEVEL_1_8);
            }

            public boolean isSource(IFluidState state) {
                return false;
            }
        }

        public static class Source extends MyFlowingFluid {
            public int getLevel(IFluidState p_207192_1_) {
                return 8;
            }

            public boolean isSource(IFluidState state) {
                return true;
            }
        }
    }
}
