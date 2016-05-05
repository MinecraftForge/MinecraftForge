package net.minecraftforge.fml.test;

import net.minecraft.entity.passive.EntityVillager.ListItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.VillagerTradePopulationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = "VillagerTradesTest", name = "VillagerTradesTest", version = "0.0.0")
public class VillagerTradesTest
{
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft:butcher")).getCareer("butcher").getTradesForCareerLevel(0).add(new ListItemForEmeralds(Items.diamond, new PriceInfo(-10, -9)));
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onTradePopulation(VillagerTradePopulationEvent event)
    {
        if(event.getVillagerCareer().getName().equals("leather"))
        {
            event.getTrades().clear();
        }
    }
} 
