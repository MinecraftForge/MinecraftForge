package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = PlayerItemPickupEventDebug.MODID, name = PlayerItemPickupEventDebug.NAME, version = PlayerItemPickupEventDebug.VERSION, acceptableRemoteVersions = "*")
public class PlayerItemPickupEventDebug
{

    private static final boolean ENABLED = false;
    public static final String MODID = "playeritempickupeventdebug";
    public static final String NAME = "Player.ItemPickup Event Debug";
    public static final String VERSION = "1.0.0";
    private static Logger logger;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void itemPickupEvent(PlayerEvent.ItemPickupEvent event)
    {
    	logger.info("Item picked up: " + event.pickedUp.getItem().getDisplayName());
    }
}
