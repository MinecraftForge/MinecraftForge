package net.minecraftforge.test;

import net.minecraft.entity.passive.EntityVillager.ListItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.VillagerTradePopulationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod(modid = "VillagerTradePopulationEventTest", name = "VillagerTradePopulationEventTest", version = "0.0.0")
public class VillagerTradePopulationEventTest
{

    @EventHandler
    public void init(FMLInitializationEvent event){
        VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("priest"));
        profession.getCareer("cleric").addTrade(1, new ListItemForEmeralds(Item.getItemFromBlock(Blocks.command_block), new PriceInfo(32, 64)));
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onVillagerTradePopulation(VillagerTradePopulationEvent event){
        if(event.getVillagerCareer().getName().equals("butcher")){
            event.getTradeList().clear();
        }
    }
}
