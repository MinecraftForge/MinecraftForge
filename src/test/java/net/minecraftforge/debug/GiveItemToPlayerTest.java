package net.minecraftforge.debug;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * A mod to test that ItemHandlerHelper.giveItemToPlayer works.
 * More specifically, that all players, including the one receiving the item, can hear the pickup sound when it happens.
 * This mod makes it so when you right click the air with a piece of dirt in your hand, you get another piece of dirt.
 * It's not a dupe glitch...it's a dupe "feature"...
 */
@Mod(modid = "giveitemtoplayertest", name = "ItemHandlerHelper.giveItemToPlayer Test", version = "1.0")
public class GiveItemToPlayerTest {
    private static final boolean ENABLED = false;

    @GameRegistry.ItemStackHolder("minecraft:dirt")
    public static final ItemStack dirt = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void rightClick(PlayerInteractEvent.RightClickItem event) {
        if (!event.getWorld().isRemote && event.getItemStack().isItemEqual(dirt)) {
            ItemHandlerHelper.giveItemToPlayer(event.getEntityPlayer(), dirt);
        }
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }
}
