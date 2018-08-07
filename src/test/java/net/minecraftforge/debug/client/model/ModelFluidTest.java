/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModelFluidTest.MODID, name = "ForgeDebugModelFluid", version = ModelFluidTest.VERSION, acceptableRemoteVersions = "*")
public class ModelFluidTest
{
    public static final String MODID = "forgedebugmodelfluid";
    public static final String VERSION = "1.0";

    public static final boolean ENABLE = false;
    private static ModelResourceLocation fluidLocation = new ModelResourceLocation(MODID + ":" + TestFluidBlock.name, "fluid");
    private static ModelResourceLocation gasLocation = new ModelResourceLocation(MODID + ":" + TestFluidBlock.name, "gas");
    private static ModelResourceLocation milkLocation = new ModelResourceLocation(MODID + ":" + TestFluidBlock.name, "milk");
    @ObjectHolder(TestFluidBlock.name)
    public static final Block FLUID_BLOCK = null;
    @ObjectHolder(TestFluidBlock.name)
    public static final Item FLUID_ITEM = null;
    @ObjectHolder(TestGasBlock.name)
    public static final Block GAS_BLOCK = null;
    @ObjectHolder(TestGasBlock.name)
    public static final Item GAS_ITEM = null;
    @ObjectHolder(MilkFluidBlock.name)
    public static final Block MILK_BLOCK = null;
    @ObjectHolder(MilkFluidBlock.name)
    public static final Item MILK_ITEM = null;

    //Used in DynBucketTest/FluidPlacementTest, TODO: Make this a full registry with @ObjectHolder?
    public static final Fluid MILK = new Fluid("milk", new ResourceLocation(ForgeVersion.MOD_ID, "blocks/milk_still"), new ResourceLocation(ForgeVersion.MOD_ID, "blocks/milk_flow"));
    public static final Fluid FLUID = new TestFluid();
    public static final Fluid GAS = new TestGas();


    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLE)
                return;

            //TODO: Make FluidRegistry a full registry?
            //Make a delegate system for FluidBlocks/Fluid Stacks?
            //Fluids must be registered before a FluidStack can be made. Which is done in the block constructor
            FluidRegistry.registerFluid(FLUID);
            FluidRegistry.registerFluid(GAS);
            FluidRegistry.registerFluid(MILK);
            event.getRegistry().registerAll(
                new TestFluidBlock(),
                new TestGasBlock(),
                new MilkFluidBlock()
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLE)
                return;
            event.getRegistry().registerAll(
                new ItemBlock(FLUID_BLOCK).setRegistryName(FLUID_BLOCK.getRegistryName()),
                new ItemBlock(GAS_BLOCK).setRegistryName(GAS_BLOCK.getRegistryName()),
                new ItemBlock(MILK_BLOCK).setRegistryName(MILK_BLOCK.getRegistryName())
            );
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLE)
                return;
            // no need to pass the locations here, since they'll be loaded by the block model logic.
            ModelBakery.registerItemVariants(FLUID_ITEM);
            ModelBakery.registerItemVariants(GAS_ITEM);
            ModelBakery.registerItemVariants(MILK_ITEM);
            ModelLoader.setCustomMeshDefinition(FLUID_ITEM, is -> fluidLocation);
            ModelLoader.setCustomMeshDefinition(GAS_ITEM, is -> gasLocation);
            ModelLoader.setCustomMeshDefinition(MILK_ITEM, is -> milkLocation);
            ModelLoader.setCustomStateMapper(FLUID_BLOCK, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return fluidLocation;
                }
            });
            ModelLoader.setCustomStateMapper(GAS_BLOCK, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return gasLocation;
                }
            });
            ModelLoader.setCustomStateMapper(MILK_BLOCK, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return milkLocation;
                }
            });
        }
    }

    public static final class TestFluid extends Fluid
    {
        public static final String name = "testfluid";

        private TestFluid()
        {
            super(name, new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_overlay"));
        }

        @Override
        public int getColor()
        {
            return 0xFF00FF00;
        }
    }

    public static final class TestGas extends Fluid
    {
        public static final String name = "testgas";

        private TestGas()
        {
            super(name, new ResourceLocation("blocks/lava_still"), new ResourceLocation("blocks/lava_flow"));
            density = -1000;
            isGaseous = true;
        }

        @Override
        public int getColor()
        {
            return 0xFFFF0000;
        }
    }

    public static final class TestFluidBlock extends BlockFluidClassic
    {
        public static final String name = "test_fluid_block";

        private TestFluidBlock()
        {
            super(FLUID, Material.WATER);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }
    }

    public static final class MilkFluidBlock extends BlockFluidClassic
    {
        public static final String name = "milk_fluid_block";

        private MilkFluidBlock()
        {
            super(MILK, Material.WATER);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }
    }

    public static final class TestGasBlock extends BlockFluidClassic
    {
        public static final String name = "test_gas_block";

        private TestGasBlock()
        {
            super(GAS, Material.LAVA);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }
    }
}
