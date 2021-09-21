package net.minecraftforge.debug.entity.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.event.entity.player.PlayerDamageItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_damage_item_event_test")
@Mod.EventBusSubscriber()
public class PlayerDamageItemEventTest 
{
    private static final boolean ENABLE = false;
    private static final Logger LOGGER = LogManager.getLogger(PlayerDamageItemEventTest.class);

    @SubscribeEvent
    public static void onPlayerDamageItemEvent(PlayerDamageItemEvent event) 
    {
        if (!ENABLE) return;
        LOGGER.info("{} damaged item {}, it has now a damage value of {}", event.getPlayer().getDisplayName().getString(), event.getStack().getDisplayName().getString(), event.getNewDamageValue());
        if (event.getPlayer().hasEffect(MobEffects.DIG_SPEED)) 
        {
            if (event.getStack().getItem() instanceof PickaxeItem) 
            {
                event.setCanceled(true);
                LOGGER.info("PlayerDamageItemEvent was canceled, because the damaged Item was an instance of PickaxeItem and the Player has the Haste Effect");
            }
        }
    }
}
