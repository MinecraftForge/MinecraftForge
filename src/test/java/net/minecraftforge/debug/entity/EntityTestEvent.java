package net.minecraftforge.debug.entity;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.debug.PotionEventTest;
import net.minecraftforge.debug.block.PistonEventTest;
import net.minecraftforge.event.entity.EndermanAttackEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "entity_event_test")
@Mod(value = "entity_event_test")
public class EntityTestEvent {

    public static final String MODID = "entity_event_test";

    @SubscribeEvent
    public static void endermanAttack(EndermanAttackEvent event)
    {

        if(event.getPlayer().getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == Items.DIAMOND_HELMET){

            event.setCanceled(true);

        }

    }

}
