package net.minecraftforge.debug;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.debug.ItemLayerModelDebug.CommonProxy;
import net.minecraftforge.debug.ItemLayerModelDebug.TestItem;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = TerrainGenEventDebug.MODID, version = TerrainGenEventDebug.VERSION)
public class TerrainGenEventDebug
{
    public static final String MODID = "TerrainGenEventDebug";
    public static final String VERSION = "1.0";

    @SidedProxy(serverSide = "net.minecraftforge.debug.TerrainGenEventDebug$CommonProxy", clientSide = "net.minecraftforge.debug.TerrainGenEventDebug$ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLInitializationEvent event) { MinecraftForge.TERRAIN_GEN_BUS.register(this); }

    public static class CommonProxy
    {
        public void Init(FMLInitializationEvent event)
        {
            MinecraftForge.TERRAIN_GEN_BUS.register(this);
        }
    }

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void Init(FMLInitializationEvent event)
        {
            super.Init(event);

        }
    }
    
        @SubscribeEvent
        public void overrideWorldGen(DecorateBiomeEvent.Decorate e){
            e.setResult(Result.DENY);
    }

}