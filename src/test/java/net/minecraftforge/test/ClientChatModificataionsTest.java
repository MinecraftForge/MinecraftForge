package net.minecraftforge.test;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatPrintedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="clientchatmodificationstest", name="Client Chat Modifications Test", version="0.0.0", clientSideOnly = true)
public class ClientChatModificataionsTest
{
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static final String CHANGE = "Welcome! This message will be modified.";
    private static final String REMOVE = "Welcome! This message will not appear.";

    @SubscribeEvent
    public void addWelcomeMessage(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (event.getWorld().isRemote && entity instanceof EntityPlayerSP)
        {
            entity.addChatMessage(new TextComponentString(CHANGE));
            entity.addChatMessage(new TextComponentString(REMOVE));
        }
    }

    @SubscribeEvent
    public void changeMessages(ClientChatPrintedEvent event)
    {
        ITextComponent component = event.getMessage();
        if (component instanceof TextComponentString)
        {
            String message = ((TextComponentString) component).getText();
            if (message.equals(CHANGE))
                event.setMessage(new TextComponentString(CHANGE + " This is the modification."));
            else if (message.equals(REMOVE))
                event.setCanceled(true);
        }
    }
}
