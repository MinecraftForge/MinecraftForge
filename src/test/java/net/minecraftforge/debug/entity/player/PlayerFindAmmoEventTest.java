package net.minecraftforge.debug.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerFindAmmoEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

@Mod("player_find_ammo_event_test")
@Mod.EventBusSubscriber
public class PlayerFindAmmoEventTest
{
    private static final boolean ENABLE = true;
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(PlayerFindAmmoEvent.class);

    @SubscribeEvent
    public static void onPlayerFindAmmo(PlayerFindAmmoEvent event)
    {
        if (!ENABLE) return;
        logger.info("The PlayerFindAmmoEvent has been called!");
        PlayerEntity player = event.getPlayer();
        logger.info("Starting search of player Ender Chest for Ammo");
        EnderChestInventory inventory = player.getInventoryEnderChest();
        ItemStack ammo = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getSizeInventory() && ammo.isEmpty(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (event.getAmmoPredicate().test(stack))
            {
                logger.info("Found valid ammo in player Ender Chest");
                ammo = stack;
                break;
            }
        }
        if (!ammo.isEmpty())
        {
            logger.info("Set found ammo to Event");
            event.setAmmo(ammo);
        }
    }
}
