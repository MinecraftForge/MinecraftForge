package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = MultiLayerModelDebug.MODID, version = MultiLayerModelDebug.VERSION)
public class MultiLayerModelDebug
{
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    public static String blockName = "test_layer_block";

    @SidedProxy
    public static CommonProxy proxy;

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.registerBlock(new Block(Material.wood)
            {
                {
                    setCreativeTab(CreativeTabs.tabBlock);
                    setUnlocalizedName(MODID + "." + blockName);
                }

                @Override
                public boolean isOpaqueCube() { return false; }

                @Override
                public boolean isFullCube() { return false; }

                @Override
                public boolean canRenderInLayer(EnumWorldBlockLayer layer)
                {
                    return layer == EnumWorldBlockLayer.SOLID || layer == EnumWorldBlockLayer.TRANSLUCENT;
                }
            }, blockName);
        }
    }

    public static class ServerProxy extends CommonProxy {}

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(GameRegistry.findBlock(MODID, blockName)), 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + blockName, "inventory"));
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }
}
