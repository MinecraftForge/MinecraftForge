package net.minecraftforge.fml.debug.simplenet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = SimpleNetworkTestMod.MOD_ID, version = SimpleNetworkTestMod.VERSION)
public class SimpleNetworkTestMod
{

    static final String MOD_ID = "debug.simplenet";
    static final String VERSION = "1.0";
    
    private static final boolean enabled = false;
    
    private static SimpleNetworkWrapper network;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!enabled) return;
        
        network = NetworkRegistry.INSTANCE.newSimpleChannel("debug");
        network.registerMessage(new TestMessage.Handler(), TestMessage.class, 0, Side.CLIENT);
        network.registerMessage(new AsyncTestMessage.ResponseHandler(), AsyncTestMessage.Response.class, 1, Side.SERVER);
        network.registerMessage(new AsyncTestMessage.Handler(), AsyncTestMessage.class, 2, Side.CLIENT);
        network.registerMessage(new TestMessage.ResponseHandler(), TestMessage.Response.class, 3, Side.SERVER);
        
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void joinHandler(PlayerLoggedInEvent event)
    {
        network.sendTo(new TestMessage(), (EntityPlayerMP) event.player);
        network.sendTo(new AsyncTestMessage(), (EntityPlayerMP) event.player);
    }
    
}
