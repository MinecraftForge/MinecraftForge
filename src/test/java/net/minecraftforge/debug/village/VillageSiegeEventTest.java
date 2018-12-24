package net.minecraftforge.debug.village;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillageSiegeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Tests {@link VillageSiegeEvent}. When enabled, players holding a diamond sword in the mainhand
 * slot are not counted in determining where the zombie siege occurs.
 */
@Mod(modid = VillageSiegeEventTest.MODID, name = VillageSiegeEventTest.NAME, version = "0.0.0", acceptableRemoteVersions = "*")
public class VillageSiegeEventTest
{
    public static final String MODID = "villagesiegeeventtest";
    public static final String NAME = "Village Siege Event Test";
    public static final boolean ENABLED = false;
    
    public static final Logger LOG = LogManager.getLogger(MODID.toUpperCase(Locale.US));
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(VillageSiegeEventTest.class);
        }
    }
    
    @SubscribeEvent
    public static void onVillageSiege(VillageSiegeEvent event)
    {
        if (!event.getWorld().isRemote && event.getPlayer().getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)
        {
            LOG.info("Village siege event for player "+event.getPlayer().getName()+" canceled");
            event.setCanceled(true);
        }
    }
}
