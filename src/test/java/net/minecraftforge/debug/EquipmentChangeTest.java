package net.minecraftforge.debug;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "equipment_change_test", version = "1.0.0")
public class EquipmentChangeTest {

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        //register the eventhandler
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * the Method handling the {@link LivingEquipmentChangeEvent}
     * Serverside only!
     */
    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        //a debug console print
        FMLLog.info("[Equipment-Change] " + event.getEntity() + " changed his Equipment in "
                + event.getSlot() + " from " + event.getFrom() + " to " + event.getTo());
    }


}
