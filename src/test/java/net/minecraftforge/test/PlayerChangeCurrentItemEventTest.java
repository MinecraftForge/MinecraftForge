package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerChangeCurrentItemEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "pccietest", name = "PlayerChangeCurrentItemEventTest", version = "1.0.0")
public class PlayerChangeCurrentItemEventTest
{
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onPlayerChangeCurrentItem(PlayerChangeCurrentItemEvent event) {
        System.out.println("The player " + event.entityPlayer.getCommandSenderName() + " has changed his current slot from " + event.entityPlayer.inventory.currentItem + " to " + event.slotIndex);
    }
}
