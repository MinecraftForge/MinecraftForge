package net.minecraftforge.debug.block;

import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.event.anvil.AnvilDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod("anvil_damage_event_test")
@EventBusSubscriber
public class AnvilDamageEventTest
{
    @SubscribeEvent
    public static void onAnvilDamage(AnvilDamageEvent event)
    {
        if (event.isCanceled()) return;
        if (event.getCurrentState().getBlock().equals(Blocks.ANVIL))
        {
            System.out.println("Cancelled Anvil Damage");
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAnvilDamagePre(AnvilDamageEvent.Falling event)
    {
        if (event.getGroundState() != null && event.getGroundState().func_235714_a_(BlockTags.WOOL))
        {
            System.out.println("Cancelled Anvil Damage from Falling");
            event.setCanceled(true);
        }
    }
}
