package net.minecraftforge.test;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="blockreplaceeventtest", name="BlockReplaceEventTest", version="0.0.0", acceptableRemoteVersions = "*")
public class BlockReplaceEventTest
{

    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockReplace(BlockEvent.ReplaceEvent event)
    {
        if(ENABLE) {
            if (event.getState() == Blocks.GRASS.getDefaultState()) {
                System.out.println("Not replacing " + event.getState().getBlock() + " with " + event.getNextState());
                event.setCanceled(true);
            }
        }
    }
}
