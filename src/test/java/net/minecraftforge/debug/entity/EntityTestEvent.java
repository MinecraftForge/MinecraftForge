package net.minecraftforge.debug.entity;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.EndermanAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "entity_event_test")
@Mod(value = "entity_event_test")
public class EntityTestEvent {


    @SubscribeEvent
    public static void endermanAttack(EndermanAttackEvent event)
    {

        if(event.getPlayer().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.DIAMOND_HELMET){

            event.setCanceled(true);

        }

    }

}
