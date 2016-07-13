package net.minecraftforge.test;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDropper;
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
        MinecraftForge.EVENT_BUS.register(PlayerInteractEventTest.class); // Test Static event listeners
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
        // The item in hand is used as well (not specifying anything would not use the item)
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
        // Note that if you do this on a chest, the f+s will fail, but then your off hand will open the chest
        // If you dual wield flints and steels and right click a chest nothing should happen
        if (evt.getItemStack() != null && evt.getItemStack().getItem() == Items.FLINT_AND_STEEL)
            evt.setUseBlock(Event.Result.DENY);

        // Case: Painting in main hand
        // Opening a TE will also place a painting on the TE if possible
        if (evt.getHand() == EnumHand.MAIN_HAND && evt.getItemStack() != null && evt.getItemStack().getItem() == Items.PAINTING) {
            evt.setUseItem(Event.Result.ALLOW);
        }

        // Spawn egg in main hand, block in offhand -> block should be placed
        // Sword in main hand, spawn egg in offhand -> nothing should happen
        if (evt.getItemStack() != null && evt.getItemStack().getItem() == Items.SPAWN_EGG) {
            evt.setCanceled(true);
        }


    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem evt)
    {
        if (!ENABLE) return;

        // Use survival mode
        // Case: Ender pearl in main hand, bow in offhand with arrows in inv -> Bow should trigger
        // Case: Sword in main hand, ender pearl in offhand -> Nothing should happen

        if (evt.getItemStack() != null && evt.getItemStack().getItem() == Items.ENDER_PEARL)
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

        if (evt.getWorld().isRemote
                && evt.getTarget() instanceof EntitySkeleton
                && evt.getLocalPos().yCoord > evt.getTarget().height / 2.0)
        {
            // If we right click the upper half of a skeleton it becomes wither skeleton. Otherwise nothing happens.
            ((EntitySkeleton) evt.getTarget()).func_189768_a(SkeletonType.WITHER);
            evt.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void interactNormal(PlayerInteractEvent.EntityInteract evt)
    {
        if (!ENABLE) return;

        if (evt.getItemStack() != null && (evt.getTarget() instanceof EntityHorse || evt.getTarget() instanceof EntityCreeper))
            // Should not be able to feed wild horses with golden apple (you will start eating it in survival)
            // Should not be able to ignite creeper with F+S
            // Applies to both hands
            evt.setCanceled(true);
    }
}
