package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.work.CapabilityWorker;
import net.minecraftforge.common.capabilities.work.IWorker;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * A simple test mod which will print the work status for worker tiles.
 * @author rubensworks
 */
@Mod(modid="forge.testcapworkermod",version="1.0")
public class TestCapabilityWorkerMod
{
    
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return;
        if (event.entityPlayer.getHeldItem() == null) return;
        if (event.entityPlayer.getHeldItem().getItem() != Items.blaze_rod) return;

        TileEntity te = event.world.getTileEntity(event.pos);
        if (te != null && te.hasCapability(CapabilityWorker.WORKER_CAPABILITY, event.face))
        {
            event.setCanceled(true);
            IWorker worker = te.getCapability(CapabilityWorker.WORKER_CAPABILITY, event.face);
            System.out.println("Has work: " + worker.hasWork());
            System.out.println("Can work: " + worker.canWork());
        }
    }
}