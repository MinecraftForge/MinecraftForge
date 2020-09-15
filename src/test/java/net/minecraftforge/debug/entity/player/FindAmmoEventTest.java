package net.minecraftforge.debug.entity.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.FindAmmoEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("find_ammo_event_test")
@Mod.EventBusSubscriber
public class FindAmmoEventTest
{
    private static final boolean ENABLE = true;
    private static final Logger LOGGER = LogManager.getLogger(FindAmmoEventTest.class);

    @SubscribeEvent
    public static void onFindAmmo(FindAmmoEvent event)
    {
        if (!ENABLE) return;
        LOGGER.info("The FindAmmoEvent has been called!");
        PlayerEntity player = event.getPlayer();
        LOGGER.info("Starting search of Player Ender Chest for Ammo");
        EnderChestInventory inventory = player.getInventoryEnderChest();
        ItemStack ammo = ItemStack.EMPTY;
        int slot = 0;
        for (int i = 0; i < inventory.getSizeInventory() && ammo.isEmpty(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (event.getAmmoPredicate().test(stack))
            {
                LOGGER.info("Found valid ammo in Player Ender Chest");
                ammo = stack;
                slot = i;
                break;
            }
        }
        if (!ammo.isEmpty())
        {
            LOGGER.info("Set found ammo to Event");
            int finalSlot = slot;
            event.setAmmo(ammo, EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, event.getShootable()) > 0 ? stack -> {
                stack.shrink(1);
                if (stack.isEmpty()) inventory.removeStackFromSlot(finalSlot);
            } : stack -> {});
        }
    }
}
