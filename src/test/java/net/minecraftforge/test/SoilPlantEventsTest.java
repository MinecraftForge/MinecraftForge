package net.minecraftforge.test;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.SoilPlantEvent.CanSoilSustainPlantEvent;
import net.minecraftforge.event.world.BlockEvent.SoilPlantEvent.GetGrowthChanceEvent;
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
	public void canSustain(CanSoilSustainPlantEvent event)
	{
		if(event.getState().getBlock() == Blocks.farmland && (event.getPlant() == Blocks.wheat || event.getPlant() == Items.wheat_seeds)) event.setCanSustain(false);
		if(event.getState().getBlock() == Blocks.diamond_block && event.getPlant() == Blocks.beetroots) event.setCanSustain(true);
	}

	@SubscribeEvent
	public void getGrowthChance(GetGrowthChanceEvent event)
	{
		if(event.getState().getBlock() == Blocks.diamond_block && event.getPlant() == Blocks.beetroots) event.setGrowthChance(event.getGrowthChance() * 10);
	}

}
