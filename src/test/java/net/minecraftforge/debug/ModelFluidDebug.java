package net.minecraftforge.debug;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ModelFluidDebug.MODID, version = ModelFluidDebug.VERSION)
public class ModelFluidDebug
{
    public static final String MODID = "ForgeDebugModelFluid";
    public static final String VERSION = "1.0";

    @SidedProxy(serverSide = "net.minecraftforge.debug.ModelFluidDebug$CommonProxy", clientSide = "net.minecraftforge.debug.ModelFluidDebug$ClientProxy")
    public static CommonProxy proxy;

    public static final Fluid milkFluid = new Fluid("milk", new ResourceLocation("forge", "blocks/milk_still"), new ResourceLocation("forge", "blocks/milk_flow"));

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            FluidRegistry.registerFluid(TestFluid.instance);
            FluidRegistry.registerFluid(TestGas.instance);
            FluidRegistry.registerFluid(milkFluid);
            GameRegistry.registerBlock(TestFluidBlock.instance, TestFluidBlock.name);
            GameRegistry.registerBlock(TestGasBlock.instance, TestGasBlock.name);
            GameRegistry.registerBlock(MilkFluidBlock.instance, MilkFluidBlock.name);
        }
    }

    public static class ClientProxy extends CommonProxy
    {
        private static ModelResourceLocation fluidLocation = new ModelResourceLocation(MODID.toLowerCase() + ":" + TestFluidBlock.name, "fluid");
        private static ModelResourceLocation gasLocation = new ModelResourceLocation(MODID.toLowerCase() + ":" + TestFluidBlock.name, "gas");
        private static ModelResourceLocation milkLocation = new ModelResourceLocation(MODID.toLowerCase() + ":" + TestFluidBlock.name, "milk");

        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            Item fluid = Item.getItemFromBlock(TestFluidBlock.instance);
            Item gas = Item.getItemFromBlock(TestGasBlock.instance);
            Item milk = Item.getItemFromBlock(MilkFluidBlock.instance);
            ModelBakery.addVariantName(fluid);
            ModelBakery.addVariantName(gas);
            ModelBakery.addVariantName(milk);
            ModelLoader.setCustomMeshDefinition(fluid, new ItemMeshDefinition()
            {
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    return fluidLocation;
                }
            });
            ModelLoader.setCustomMeshDefinition(gas, new ItemMeshDefinition()
            {
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    return gasLocation;
                }
            });
            ModelLoader.setCustomMeshDefinition(milk, new ItemMeshDefinition()
            {
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    return milkLocation;
                }
            });
            ModelLoader.setCustomStateMapper(TestFluidBlock.instance, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return fluidLocation;
                }
            });
            ModelLoader.setCustomStateMapper(TestGasBlock.instance, new StateMapperBase()
            {
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return gasLocation;
                }
            });
            ModelLoader.setCustomStateMapper(MilkFluidBlock.instance, new StateMapperBase()
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
        public static final TestFluid instance = new TestFluid();

        private TestFluid()
        {
            super(name, new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow"));
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
        public static final TestGas instance = new TestGas();

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
        public static final TestFluidBlock instance = new TestFluidBlock();
        public static final String name = "TestFluidBlock";

        private TestFluidBlock()
        {
            super(TestFluid.instance, Material.water);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
    }

    public static final class MilkFluidBlock extends BlockFluidClassic
    {
        public static final MilkFluidBlock instance = new MilkFluidBlock();
        public static final String name = "MilkFluidBlock";

        private MilkFluidBlock()
        {
            super(milkFluid, Material.water);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
    }

    public static final class TestGasBlock extends BlockFluidClassic
    {
        public static final TestGasBlock instance = new TestGasBlock();
        public static final String name = "TestGasBlock";

        private TestGasBlock()
        {
            super(TestGas.instance, Material.lava);
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
    }
}
