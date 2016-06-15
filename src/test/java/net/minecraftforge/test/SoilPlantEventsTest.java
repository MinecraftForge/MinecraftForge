package net.minecraftforge.test;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.GetGrowthChanceEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="SoilPlantEventsTest", name="SoilPlantEventsTest", version="0.0.0")
public class SoilPlantEventsTest
{

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void getGrowthChance(GetGrowthChanceEvent event)
    {
        if(event.getPlant() == Blocks.BEETROOTS) event.setGrowthChance(event.getGrowthChance() * 10);
    }

}