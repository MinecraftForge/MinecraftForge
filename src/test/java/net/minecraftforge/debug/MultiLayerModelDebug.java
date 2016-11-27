package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = MultiLayerModelDebug.MODID, name = "ForgeDebugMultiLayerModel", version = MultiLayerModelDebug.VERSION, acceptableRemoteVersions = "*")
public class MultiLayerModelDebug
{
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    private static String blockName = "test_layer_block";
    private static final ResourceLocation blockId = new ResourceLocation(MODID, blockName);

    @SidedProxy
    public static CommonProxy proxy;

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.register(new Block(Material.WOOD)
            {
                {
                    setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
                    setUnlocalizedName(MODID + "." + blockName);
                    setRegistryName(blockId);
                }

                @Override
                public boolean isOpaqueCube(IBlockState state) { return false; }

                @Override
                public boolean isFullCube(IBlockState state) { return false; }

                @Override
                public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
                {
                    return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
                }
            });
            GameRegistry.register(new ItemBlock(Block.REGISTRY.getObject(blockId)).setRegistryName(blockId));
        }
    }

    public static class ServerProxy extends CommonProxy {}

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(blockId), 0, new ModelResourceLocation(blockId, "inventory"));
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }
}
