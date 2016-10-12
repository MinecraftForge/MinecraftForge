package net.minecraftforge.debug;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ItemLayerModelDebug.MODID, version = ItemLayerModelDebug.VERSION)
public class ItemLayerModelDebug
{
    public static final String MODID = "ForgeDebugItemLayerModel";
    public static final String VERSION = "1.0";

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.registerItem(TestItem.instance, TestItem.name);
        }
    }

    public static class ServerProxy extends CommonProxy {}

    public static class ClientProxy extends CommonProxy
    {
        private static ModelResourceLocation modelLocation = new ModelResourceLocation(MODID.toLowerCase() + ":" + TestItem.name, "inventory");
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            ModelLoader.setCustomModelResourceLocation(TestItem.instance, 0, modelLocation);
        }
    }

    public static final class TestItem extends Item
    {
        public static final TestItem instance = new TestItem();
        public static final String name = "TestItem";

        private TestItem()
        {
            setCreativeTab(CreativeTabs.tabBlock);
            setUnlocalizedName(MODID + ":" + name);
        }
    }
}
