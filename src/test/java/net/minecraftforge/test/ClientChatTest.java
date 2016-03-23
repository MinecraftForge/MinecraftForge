package net.minecraftforge.test;

import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
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
 * <br>
 * And helps you keep track of how many words you have said <br>
 */
@Mod(modid = "clientChatEventTest", name = "Client Chat Event Test", version = "0.0.0")
public class ClientChatTest
{
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private int totalWords = 0;

    @SubscribeEvent
    public void replaceChat(ClientChatEvent event)
    {
        /* example 1 - replacing words */

        String msg = event.getMessage();

        msg = msg.replace("modloader", "Forge");
        msg = msg.replace("lol", "laughs out loud!");

        event.setMessage(msg); // replace the message

        /* example 2 - analyzing the users chat */

        totalWords += msg.split(" ").length;
        event.getSender().addChatMessage(new ChatComponentText(
                "You have said " + totalWords + " word so far."));
    }
}
