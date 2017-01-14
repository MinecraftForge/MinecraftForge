package net.minecraftforge.debug;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

@Mod(modid = ModelFluidDebug.MODID, name = "ForgeDebugModelFluid", version = ModelFluidDebug.VERSION, acceptableRemoteVersions = "*")
public class ModelFluidDebug
{
    public static final String MODID = "forgedebugmodelfluid";
    public static final String VERSION = "1.0";

    public static final boolean ENABLE = true;

    @SidedProxy
    public static CommonProxy proxy;

    public static final Fluid milkFluid = new Fluid("milk", new ResourceLocation(ForgeVersion.MOD_ID, "blocks/milk_still"), new ResourceLocation(ForgeVersion.MOD_ID, "blocks/milk_flow"));

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            proxy.preInit(event);
        }
    }

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            FluidRegistry.registerFluid(TestFluid.instance);
            FluidRegistry.registerFluid(TestGas.instance);
            FluidRegistry.registerFluid(milkFluid);
            GameRegistry.register(TestFluidBlock.instance);
            GameRegistry.register(new ItemBlock(TestFluidBlock.instance).setRegistryName(TestFluidBlock.instance.getRegistryName()));
            GameRegistry.register(TestGasBlock.instance);
            GameRegistry.register(new ItemBlock(TestGasBlock.instance).setRegistryName(TestGasBlock.instance.getRegistryName()));
            GameRegistry.register(MilkFluidBlock.instance);
            GameRegistry.register(new ItemBlock(MilkFluidBlock.instance).setRegistryName(MilkFluidBlock.instance.getRegistryName()));
        }
    }

    public static class ServerProxy extends CommonProxy {}

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
            // no need to pass the locations here, since they'll be loaded by the block model logic.
            ModelBakery.registerItemVariants(fluid);
            ModelBakery.registerItemVariants(gas);
            ModelBakery.registerItemVariants(milk);
            ModelLoader.setCustomMeshDefinition(fluid, new ItemMeshDefinition()
            {
                public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack)
                {
                    return fluidLocation;
                }
            });
            ModelLoader.setCustomMeshDefinition(gas, new ItemMeshDefinition()
            {
                public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack)
                {
                    return gasLocation;
                }
            });
            ModelLoader.setCustomMeshDefinition(milk, new ItemMeshDefinition()
            {
                public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack)
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
        public static final String name = "test_fluid_block";

        private TestFluidBlock()
        {
            super(TestFluid.instance, Material.WATER);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }
    }

    public static final class MilkFluidBlock extends BlockFluidClassic
    {
        public static final MilkFluidBlock instance = new MilkFluidBlock();
        public static final String name = "milk_fluid_block";

        private MilkFluidBlock()
        {
            super(milkFluid, Material.WATER);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }
    }

    public static final class TestGasBlock extends BlockFluidClassic
    {
        public static final TestGasBlock instance = new TestGasBlock();
        public static final String name = "test_gas_block";

        private TestGasBlock()
        {
            super(TestGas.instance, Material.LAVA);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }
    }
}
