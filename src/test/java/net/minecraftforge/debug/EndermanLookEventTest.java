package net.minecraftforge.debug;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.player.EndermanLookEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EndermanLookEventTest.MOD_ID, name = "EndermanLookEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class EndermanLookEventTest {
    static final String MOD_ID = "enderman_look_event";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onLookAtEnderman(EndermanLookEvent event) {
        if (!ENABLED) {
            return;
        }
        if (event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().equals(Items.DIAMOND_HELMET)) {
            event.setCanceled(true);
        }

    }
}
