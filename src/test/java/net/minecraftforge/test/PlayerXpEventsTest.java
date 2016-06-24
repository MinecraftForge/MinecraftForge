package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerAddXpEvent;
import net.minecraftforge.event.entity.player.PlayerChangeLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This is a test for {@link PlayerChangeLevelEvent} and {@link PlayerAddXpEvent}
 */
@Mod(modid = "playerxpeventtest", name = "Player Xp Event Test", version = "0.0.0")
public class PlayerXpEventsTest
{
    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAddXp(PlayerAddXpEvent event)
    {
        if (!ENABLE) return;
        System.out.println("Adding " + event.getAmount() + "XP to " + event.getEntityPlayer().getName());
    }

    @SubscribeEvent
    public void onLevelChange(PlayerChangeLevelEvent event)
    {
        if (!ENABLE) return;
        System.out.println("Changing " + event.getEntityPlayer().getName() + "'s XP Level by " + event.getLevels());
    }
}
