package net.minecraftforge.debug.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.living.LivingFindAmmoEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("living_find_ammo_event_test")
@Mod.EventBusSubscriber
public class LivingFindAmmoEventTest
{
    private static final boolean ENABLE = true;
    private static final Logger LOGGER = LogManager.getLogger(LivingFindAmmoEvent.class);

    @SubscribeEvent
    public static void onLivingFindAmmo(LivingFindAmmoEvent event)
    {
        if (!ENABLE) return;
        if (event.getEntityLiving() instanceof PlayerEntity)
        {
            LOGGER.info("The LivingFindAmmoEvent has been called!");
            LOGGER.info("Firing Player targeted code!");
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            LOGGER.info("Starting search of player Ender Chest for Ammo");
            EnderChestInventory inventory = player.getInventoryEnderChest();
            ItemStack ammo = ItemStack.EMPTY;
            int slot = 0;
            for (int i = 0; i < inventory.getSizeInventory() && ammo.isEmpty(); i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                if (event.getAmmoPredicate().test(stack))
                {
                    LOGGER.info("Found valid ammo in player Ender Chest");
                    ammo = stack;
                    slot = i;
                    break;
                }
            }
            if (!ammo.isEmpty())
            {
                LOGGER.info("Set found ammo to Event");
                int finalSlot = slot;
                event.setAmmo(ammo, stack -> {
                    stack.shrink(1);
                    if (stack.isEmpty()) inventory.removeStackFromSlot(finalSlot);
                });
            }
        } else
        {
            event.setAmmo(new ItemStack(Items.SPECTRAL_ARROW), stack -> {});
        }
    }
}