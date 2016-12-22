package net.minecraftforge.test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "is_book_enchantable_test", name = "Test for isBookEnchantable", version = "1.0")
@Mod.EventBusSubscriber
public class IsBookEnchantableTest
{
    private static final Item TEST_ITEM = new TestItem();

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(TEST_ITEM);
    }

    private static class TestItem extends Item
    {
        private static final String NAME = "test_item";

        private TestItem()
        {
            setRegistryName(NAME);
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book)
        {
            return false;
        }
    }
}
