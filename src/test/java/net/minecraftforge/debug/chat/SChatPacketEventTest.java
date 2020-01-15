package net.minecraftforge.debug.chat;

import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.SChatPacketEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("schatpacket_event_test")
@Mod.EventBusSubscriber
public class SChatPacketEventTest
{
    @SubscribeEvent
    public static void onSChatPacket(SChatPacketEvent event)
    {
        if(event.getOriginalPacket().getChatComponent().getUnformattedComponentText().contains("CancelSChatPacket"))
            event.setCanceled(true);
        else if(event.getOriginalPacket().getChatComponent().getUnformattedComponentText().contains("ReplaceSChatPacket"))
            event.setModifiedPacket(new SChatPacket(new StringTextComponent("Chat Packet Replaced."), event.getOriginalPacket().getType()));
    }
}
