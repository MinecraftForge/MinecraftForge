package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ClientChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This is a mod to test ClientChatEvent. <br>
 * <br>
 * It replaces words in chat with better ones ;) <br>
 * "modloader" -> "Forge" <br>
 * "lol" -> "laughs out loud!"
 */
@Mod(modid="clientChatEventTest", name="Client Chat Event Test", version="0.1")
public class ClientChatTest
{
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void replaceChat(ClientChatEvent event){
        String msg = event.message;
        
        msg = msg.replace("modloader", "Forge");
        msg = msg.replace("lol", "laughs out loud!");
        
         // cancel the event so we don't send the old and the new massage
        event.setCanceled(true);
        event.sender.sendChatMessage(msg);
    }
}
