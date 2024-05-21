package net.minecraftforge.debug.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CustomizeGuiOverlayEventTest.MODID)
public class CustomizeGuiOverlayEventTest
{
    private static final boolean ENABLED = true;

    public static final String MODID = "cust_gui_event_test";
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents {
        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onRenderOverlayDebug(CustomizeGuiOverlayEvent.DebugText event) {
            if(ENABLED) {
                if(CustomizeGuiOverlayEvent.DebugText.Side.Left.equals(event.getSide())) {
                    event.getText().add("Left Side");
                } else {
                    event.getText().add("Right Side");
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.NORMAL)
        public void onRenderOverlaychat(CustomizeGuiOverlayEvent.Chat event) {
            if(ENABLED) {
                event.setPosY(event.getPosY() - 100);
            }
        }
    }
}
