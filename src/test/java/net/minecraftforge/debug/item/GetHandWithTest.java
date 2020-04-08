package net.minecraftforge.debug.item;

import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(GetHandWithTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GetHandWithTest {

    //Testing if the new alternative for ProjectileHelper.getHandWith works
    //Skeletons and Illusioners should be able to use the modded bow;
    //and Pillagers should be able to use the modded crossbow.

    static final String MODID = "get_hand_with_test";

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BowItem(new Item.Properties().group(ItemGroup.COMBAT).maxDamage(256)).setRegistryName(MODID, "modded_bow"));
        event.getRegistry().register(new CrossbowItem(new Item.Properties().group(ItemGroup.COMBAT).maxDamage(326)).setRegistryName(MODID, "modded_crossbow"));
    }

}
