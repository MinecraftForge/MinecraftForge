package net.minecraftforge.test;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid="PlayerInteractEventTest", name="PlayerInteractEventTest", version="0.0.0")
public class PlayerInteractEventTest
{
    // NOTE: Test with both this ON and OFF - ensure none of the test behaviours show when this is off!
    private static final boolean ENABLE = false;
    private Logger logger;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(receiveCanceled = true) // this triggers after the subclasses below, and we'd like to log them all
    public void global(PlayerInteractEvent evt)
    {
        if (!ENABLE) return;
        logger.info("{} | {}", evt.getClass().getSimpleName(), evt.getSide().name());
        logger.info("{} | stack: {}", evt.getHand(), evt.getItemStack());
        logger.info("{} | face: {}", evt.getPos(), evt.getFace());
    }

    @SubscribeEvent
    public void leftClickBlock(PlayerInteractEvent.LeftClickBlock evt)
    {
        if (!ENABLE) return;
        logger.info("HIT VEC: {}", evt.getHitVec());

        if (evt.getItemStack() != null)
        {
            if (evt.getItemStack().getItem() == Items.GOLDEN_PICKAXE)
                evt.setCanceled(true); // Redstone should not activate and pick should not be able to dig anything
            if (evt.getItemStack().getItem() == Items.DIAMOND_PICKAXE)
                evt.setUseBlock(Event.Result.DENY); // Redstone should not activate, pick should still dig
            if (evt.getItemStack().getItem() == Items.IRON_PICKAXE)
                evt.setUseItem(Event.Result.DENY); // Pick should not dig, Redstone should still activate
        }

        // When item use denied, the event will keep firing as long as the left click button is held.
        // This is due to how vanilla calls the left click handling methods to let people not lift their button when mining multiple blocks.
        // Note that when item use is denied, the cool down for the item does not occur. This is good!
    }

    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent.RightClickBlock evt)
    {
        if (!ENABLE) return;
        logger.info("HIT VEC: {}", evt.getHitVec());

        // Shift right clicking dropper with an item in hand should still open the dropper contrary to normal mechanics
        // The item in hand is used as well (not specifying anything would not use the item) - a splash potion works well for testing this.
        TileEntity te = evt.getWorld().getTileEntity(evt.getPos());
        if (te instanceof TileEntityDropper)
        {
            evt.setUseBlock(Event.Result.ALLOW);
            evt.setUseItem(Event.Result.ALLOW);
        }

        // Same as above, except the item should no longer be used
        if (te instanceof TileEntityChest)
        {
            evt.setUseBlock(Event.Result.ALLOW);
            evt.setUseItem(Event.Result.DENY); // could be left out as well
        }

        // Case: Flint and steel in main hand on top of a TE will light a fire, not open the TE.
        // Note that if you do this on a chest, the f+s will fail (since chests aren't full blocks), but then your off hand will open the chest
        // If you dual wield flints and steels and right click a chest nothing should happen
        if (evt.getItemStack() != null && evt.getItemStack().getItem() == Items.FLINT_AND_STEEL)
            evt.setUseBlock(Event.Result.DENY);

        // Case: Painting in main hand
        // Opening a TE will also place a painting on the TE if possible
        if (evt.getHand() == EnumHand.MAIN_HAND && evt.getItemStack() != null && evt.getItemStack().getItem() == Items.PAINTING) {
            evt.setUseItem(Event.Result.ALLOW);
        }

        // Spawn egg in main hand, block in offhand -> block should be placed
        if (evt.getItemStack() != null
                && evt.getItemStack().getItem() == Items.SPAWN_EGG
                && evt.getHand() == EnumHand.MAIN_HAND
                && evt.getEntityPlayer().getHeldItemOffhand() != null
                && evt.getEntityPlayer().getHeldItemOffhand().getItem() instanceof ItemBlock) {
            evt.setCanceled(true);
        }

        // Spawn egg in main hand, block in offhand -> potion should NOT be thrown
        if (evt.getItemStack() != null
                && evt.getItemStack().getItem() == Items.SPAWN_EGG
                && evt.getHand() == EnumHand.MAIN_HAND
                && evt.getEntityPlayer().getHeldItemOffhand() != null
                && evt.getEntityPlayer().getHeldItemOffhand().getItem() == Items.SPLASH_POTION) {
            evt.setCanceled(true);
            evt.setSubstituteResult(EnumActionResult.SUCCESS); // Fake spawn egg success so splash potion does not trigger
        }


    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem evt)
    {
        if (!ENABLE) return;

        // Case: Ender pearl in main hand, block in offhand -> Block is NOT placed
        if (evt.getItemStack() != null
                && evt.getItemStack().getItem() == Items.ENDER_PEARL
                && evt.getHand() == EnumHand.MAIN_HAND
                && evt.getEntityPlayer().getHeldItemOffhand() != null
                && evt.getEntityPlayer().getHeldItemOffhand().getItem() instanceof ItemBlock)
        {
            evt.setCanceled(true);
            evt.setSubstituteResult(EnumActionResult.SUCCESS); // We fake success on the ender pearl so block is not placed
            return;
        }

        // Case: Ender pearl in main hand, bow in offhand with arrows in inv -> Bow should trigger
        // Case: Sword in main hand, ender pearl in offhand -> Nothing should happen
        if (evt.getItemStack() != null
                && evt.getItemStack().getItem() == Items.ENDER_PEARL
                && evt.getHand() == EnumHand.MAIN_HAND
                && evt.getEntityPlayer().getHeldItemOffhand() != null
                && evt.getEntityPlayer().getHeldItemOffhand().getItem() instanceof ItemBlock)
            evt.setCanceled(true);
    }

    @SubscribeEvent
    public void interactSpecific(PlayerInteractEvent.EntityInteractSpecific evt)
    {
        if (!ENABLE) return;
        logger.info("LOCAL POS: {}", evt.getLocalPos());

        if (evt.getItemStack() != null
                && evt.getTarget() instanceof EntityArmorStand
                && evt.getItemStack().getItem() == Items.IRON_HELMET)
            evt.setCanceled(true); // Should not be able to place iron helmet onto armor stand (you will put it on instead)

        if (evt.getItemStack() != null
                && evt.getTarget() instanceof EntityArmorStand
                && evt.getItemStack().getItem() == Items.GOLDEN_HELMET)
        {
            evt.setCanceled(true);
            evt.setSubstituteResult(EnumActionResult.SUCCESS);
            // Should not be able to place golden helmet onto armor stand
            // However you will NOT put it on because we fake success on the armorstand.
        }

        if (!evt.getWorld().isRemote
                && evt.getTarget() instanceof EntitySkeleton
                && evt.getLocalPos().yCoord > evt.getTarget().height / 2.0)
        {
            // If we right click the upper half of a skeleton it becomes wither skeleton. Otherwise nothing happens.
            ((EntitySkeleton) evt.getTarget()).setSkeletonType(1);
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void interactNormal(PlayerInteractEvent.EntityInteract evt)
    {
        if (!ENABLE) return;

        if (evt.getItemStack() != null && evt.getTarget() instanceof EntityHorse) {
            // Should not be able to feed wild horses with golden apple (you will start eating it in survival)
            if (evt.getItemStack().getItem() == Items.GOLDEN_APPLE
                    && evt.getItemStack().getItemDamage() == 0)
                evt.setCanceled(true);
            // Should not be able to feed wild horses with notch apple but you will NOT eat it
            if (evt.getItemStack().getItem() == Items.GOLDEN_APPLE
                    && evt.getItemStack().getItemDamage() == 1)
            {
                evt.setCanceled(true);
                evt.setSubstituteResult(EnumActionResult.SUCCESS);
            }
        }
    }
}
