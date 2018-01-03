package net.minecraftforge.debug;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.living.EndermanLookAggroEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EndermanLookAggroEventTest.MOD_ID, name = "EndermanLookAggroEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class EndermanLookAggroEventTest {
    static final String MOD_ID = "enderman_look_aggro_event";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onLookAtEnderman(EndermanLookAggroEvent event) {
        if (!ENABLED) {
            return;
        }
        if (event.getTarget().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().equals(Items.DIAMOND_HELMET)) {
            event.setResult(Event.Result.DENY);
        }

    }
}
