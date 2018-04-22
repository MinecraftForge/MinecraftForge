package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CanApplyAtEnchantingTableTest.MODID, name = "CanApplyAtEnchantingTableTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class CanApplyAtEnchantingTableTest
{
    public static final String MODID = "can_apply_at_enchanting_table_test";
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
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (ENABLE)
            {
                event.getRegistry().register(
                        testItem.setRegistryName("test_item")
                                .setUnlocalizedName("FortuneEnchantableOnly")
                                .setMaxStackSize(1));
            }
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (ENABLE)
            {
                ModelBakery.registerItemVariants(testItem);
            }
        }
    }
}
