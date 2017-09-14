package net.minecraftforge.debug;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = CanApplyAtEnchantingTableTest.MODID, name = "CanApplyAtEnchantingTableTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class CanApplyAtEnchantingTableTest
{
    public static final String MODID = "canapplyatenchantingtabletest";
    public static final boolean ENABLE = false;

    public static final Item testItem = new Item()
    {
        @Override
        public boolean isEnchantable(ItemStack stack)
        {
            return true;
        }

        @Override
        public int getItemEnchantability()
        {
            return 30;
        }

        @Override
        public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
        {
            return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.FORTUNE;
        }
    };

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registrItems(RegistryEvent.Register<Item> event)
        {
            if (ENABLE)
                event.getRegistry().register(testItem.setRegistryName("test_item").setUnlocalizedName("FortuneEnchantableOnly").setMaxStackSize(1));
        }
    }
}
