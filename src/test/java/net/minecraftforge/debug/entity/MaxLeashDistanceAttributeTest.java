package net.minecraftforge.debug.entity;

import com.google.common.collect.Multimap;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(MaxLeashDistanceAttributeTest.MOD_ID)
@Mod.EventBusSubscriber
public class MaxLeashDistanceAttributeTest
{
    public static final String MOD_ID = "max_leash_distance_attribute_test";
    private static final String ITEM_ID = "extended_leash_plate";
    private static final Item PLATE = new ExtendedLeashPlate().setRegistryName(MOD_ID, ITEM_ID);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e)
    {
        e.getRegistry().register(PLATE);
    }
    
    public static class ExtendedLeashPlate extends ArmorItem
    {
        private static final AttributeModifier BOOST = new AttributeModifier("extended leash plate boost", 3, AttributeModifier.Operation.ADDITION);
        
        public ExtendedLeashPlate()
        {
            super(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT));
        }

        @Override
        public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot)
        {
            Multimap<String, AttributeModifier> attribs = super.getAttributeModifiers(slot);
            if (slot == this.slot)
            {
                attribs.put(CreatureEntity.MAX_LEASH_DISTANCE.getName(), BOOST);
            }
            return attribs;
        }
    }
}
