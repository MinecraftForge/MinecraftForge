package net.minecraftforge.debug.network;

import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerTryConnectEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/*
 * When this mod is enabled the behavior should be as follows:
 * - If a player is banned and no online players are holding sticks, it modifies the message informing them they are banned to mention the lack of stick
 * - If a player is banned and an online player is holding a stick, they can get in
 * - If a player would be able to get in normally, but a player is holding a stick, they are rejected informing them that a player is holding a stick
 * 
 * This covers all the main checks:
 * - That behavior can change at any time
 * - That the message can be modified
 * - That an acceptance can be turned into a rejection
 * - That a rejection can be turned into an acceptance
 * 
 * Sticks form an easy test criteria
 */

@Mod(modid = ServerModifyConnectTest.MODID, name = ServerModifyConnectTest.NAME, version = "1.0.0", acceptedMinecraftVersions = "*", acceptableRemoteVersions = "*")
public class ServerModifyConnectTest
{

    static final boolean ENABLED = false;      // <-- enable mod

    static final String MODID = "servermodifyconnecttest";
    static final String NAME = "Server Modify Connect Test";

    private static Logger logger;


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            logger.info("Started up stick based access filtering");
            MinecraftForge.EVENT_BUS.register(ServerModifyConnectTest.class);
        }
    }

    private static boolean isPlayerHoldingStick(MinecraftServer server)
    {
        for(EntityPlayerMP player : server.getPlayerList().getPlayers())
        {
            ItemStack stack = player.getHeldItemMainhand();
            if(stack != null && stack.getItem().getUnlocalizedName().equals("item.stick"))
                return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onTryConnect(final ServerTryConnectEvent event)
    {
        logger.info("A player is trying to connect");
        if(event.isAccepted())
        {
            logger.info("That player would normally be accepted");
            if(isPlayerHoldingStick(event.getServer()))
            {
                logger.info("...except someone is holding a stick");
                event.reject("A player is holding a stick");
            }
            else
            {
                logger.info("and no one is holding a stick!");
            }
        }
        else if(event.getRejectionMessage().startsWith("You are banned from this server!"))
        {
            logger.info("That player is banned");
            if(isPlayerHoldingStick(event.getServer()))
            {
                logger.info("but someone is holding a stick!");
                event.accept();
            }
            else
            {
                logger.info("...and no one is holding a stick");
                event.reject("You are banned and no player is holding a stick");
            }
        }
        else
        {
            logger.info("That player wouldn't be accepted, but isn't banned");
        }
    }
}