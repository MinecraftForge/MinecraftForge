package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.PickBlockEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "pickblockeventtest", name = "Pick Block Event Test", version = "0.0.0", clientSideOnly = true)
public class PickBlockEventTest
{
    static final boolean ENABLED = true;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    int testCycle = 0;

    @SubscribeEvent
    public void onPickBlockEvent(PickBlockEvent event)
    {
        switch( testCycle )
        {
            case 0:
                logger.info("Canceled, nothing should have been picked.");
                event.setCanceled( true );
                break;

            case 1:
                event.setPickResult(new ItemStack(Items.APPLE));
                logger.info("Overwritten with {}", event.getPickResult().getDisplayName());
                logger.info("OriginalResult: {}", event.getOriginalPickResult().getDisplayName());
                break;

            case 2:
                logger.info("No Change, default behavior.");
                break;
        }

        testCycle =  (testCycle + 1 ) % 3;
    }
}
