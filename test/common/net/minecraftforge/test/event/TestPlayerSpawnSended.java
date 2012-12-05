package net.minecraftforge.test.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerSpawnSendedEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Minecraft Forge PlayerSpawnSended Event Test #1
 * 
 * @author mrTJO
 * @since 1.4.5
 * @version 0.1
 */
@Mod(modid="ForgeTE.EVT-02", name="Forge Test - PlayerSpawnSended Event Test #1", version="0.1")
public class TestPlayerSpawnSended
{
    @PreInit
    public void onPreInit(FMLPreInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @ForgeSubscribe
    public void onPlayerSpawnSended(PlayerSpawnSendedEvent evt)
    {
        System.out.println("Sending "+evt.getSpawnedPlayer().username+" to "+evt.getReceiverPlayer().username);
    }
}
