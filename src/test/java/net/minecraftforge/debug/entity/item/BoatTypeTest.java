package net.minecraftforge.debug.entity.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DispenseBoatBehavior;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.debug.entity.item.BoatTypeTest.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BoatTypeTest {

    static final String MOD_ID = "boat_type_test";

    private static final String BOAT_KEY = "test";
    private static final String ITEM_KEY = "test_boat";

    private static final BoatEntity.Type GREEN_BOAT_TYPE = BoatEntity.Type.create("GREEN", Blocks.GREEN_WOOL, MOD_ID + ":" + BOAT_KEY, () -> BoatTypeTest.TEST_BOAT_ITEM);
    private static final BoatItem TEST_BOAT_ITEM = new BoatItem(GREEN_BOAT_TYPE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION));

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        TEST_BOAT_ITEM.setRegistryName(MOD_ID, ITEM_KEY);
        event.getRegistry().register(TEST_BOAT_ITEM);
        DispenserBlock.registerDispenseBehavior(TEST_BOAT_ITEM, new DispenseBoatBehavior(GREEN_BOAT_TYPE));
    }

}
