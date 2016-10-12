package net.minecraftforge.test;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerSendChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author shadowfacts
 */
@Mod(modid = "ServerSendChatTest")
public class ServerSendChatTest
{

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerSendChat(ServerSendChatEvent event) {
        event.setComponent(new TextComponentString("[From server] ").appendSibling(event.getComponent()));
    }

}
