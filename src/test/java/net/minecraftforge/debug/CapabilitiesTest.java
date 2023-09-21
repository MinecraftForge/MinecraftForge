package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mod(CapabilitiesTest.MOD_ID)
public class CapabilitiesTest {
    public static final String MOD_ID = "captest";
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> TRACK = new ConcurrentHashMap<>();
    private static final boolean ENABLED = false;

    public static void logAttachEvent(Event event) {
        TRACK.computeIfAbsent(event.getClass(), e -> new AtomicInteger()).getAndAdd(1);
    }

    public CapabilitiesTest() {
        if (ENABLED) // Register our listeners if this test is enabled.
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void AttachItemStack(AttachCapabilitiesEvent.AttachItemStackEvent event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachBlockEntity(AttachCapabilitiesEvent.AttachBlockEntityEvent event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachLevel(AttachCapabilitiesEvent.AttachLevelEvent event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachLevelChunk(AttachCapabilitiesEvent.AttachLevelChunkEvent event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachEntity(AttachCapabilitiesEvent.AttachEntityEvent event) {
        logAttachEvent(event);
    }

    @SubscribeEvent
    public void AttachBucketItem(AttachCapabilitiesEvent.AttachBucketItemStackEvent event) {
        logAttachEvent(event);
    }




    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid = CapabilitiesTest.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents
    {

        private static final int tickRate = 100;
        private static int ticks = 0;
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event)
        {
            if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null)
            {
                ticks++;
                if (ticks % tickRate == 0) {
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft != null && minecraft.player != null) {
                        Player player = minecraft.player;

                        player.sendSystemMessage(Component.literal("Start of CapabilitiesTest results for the last %s ticks".formatted(tickRate)));

                        TRACK.forEach(((aClass, atomicInteger) -> {
                            player.sendSystemMessage(Component.literal("AttachEvent: %s heard %s times".formatted(
                                    aClass.getSimpleName(),
                                    atomicInteger.getAndSet(0)
                            )));
                        }));

                        player.sendSystemMessage(Component.literal("End of CapabilitiesTest results for the last %s ticks".formatted(tickRate)));
                    }
                }
            }
        }
    }
}
