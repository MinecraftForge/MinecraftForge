package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
@Mod (modid = FogColorInsideMaterialTest.MOD_ID, name = "FogColor inside material debug.", version = "1.0", acceptableRemoteVersions = "*")
public class FogColorInsideMaterialTest
{

    public static final String MOD_ID = "fogcolorinsidematerialtest";

    @ObjectHolder ("test_fluid")
    public static final Block FLUID_BLOCK = null;
    @ObjectHolder ("test_fluid")
    public static final Item FLUID_ITEM = null;

    private static final ResourceLocation testFluidRegistryName = new ResourceLocation(MOD_ID, "test_fluid");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        Fluid fluid = new Fluid("fog_test", Blocks.WATER.getRegistryName(), Blocks.FLOWING_WATER.getRegistryName());
        FluidRegistry.registerFluid(fluid);
        Block fluidBlock = new BlockFluidClassic(fluid, Material.WATER)
        {
            @Override
            public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
            {
                return new Vec3d(0.6F, 0.1F, 0.0F);
            }
        };
        fluidBlock.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        fluidBlock.setUnlocalizedName(MOD_ID + ".test_fluid");
        fluidBlock.setRegistryName(testFluidRegistryName);
        event.getRegistry().register(fluidBlock);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(FLUID_BLOCK).setRegistryName(testFluidRegistryName));
    }

    @EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelResourceLocation fluidLocation = new ModelResourceLocation(testFluidRegistryName, "fluid");
            ModelLoader.registerItemVariants(FLUID_ITEM);
            ModelLoader.setCustomMeshDefinition(FLUID_ITEM, stack -> fluidLocation);
            ModelLoader.setCustomStateMapper(FLUID_BLOCK, new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state)
                {
                    return fluidLocation;
                }
            });
        }
    }

}
