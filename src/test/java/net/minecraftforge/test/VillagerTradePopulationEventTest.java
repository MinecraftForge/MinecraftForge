package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.VillagerTradePopulationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "VillagerTradePopulationEventTest", name = "VillagerTradePopulationEventTest", version = "0.0.0")
public class VillagerTradePopulationEventTest
{

    @EventHandler
    public void init(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onVillagerTradePopulation(VillagerTradePopulationEvent event){
        if(event.getVillagerCareer().getName().equals("butcher")){
            event.getTradeList().clear();
        }
    }
}
