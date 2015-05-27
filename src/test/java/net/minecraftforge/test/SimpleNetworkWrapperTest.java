package net.minecraftforge.test;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.debug.ModelBakeEventDebug.BakeEventHandler;
import net.minecraftforge.debug.ModelBakeEventDebug.CommonProxy;
import net.minecraftforge.debug.ModelBakeEventDebug.CustomModelBlock;
import net.minecraftforge.debug.ModelBakeEventDebug.CustomTileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/*
 * Test for tweaks to SimpleNetworkWrapper:
 * registerMessageType and registerMessageHandler instead of registerMessage
 * 
 * Test is:
 * Every 5 seconds, server sends a TestMessage to all clients.
 * Client receives message and prints to console.
 */

@Mod(modid="SimpleNetworkWrapperTest", name="SimpleNetworkWrapperTest", version="0.0.0")
public class SimpleNetworkWrapperTest  {
    public static final boolean ENABLE = true;

    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        if (ENABLE) {
            proxy.init(event);
            FMLCommonHandler.instance().bus().register(new ServerTickMessageSender());
        }
    }
    
    @SidedProxy(serverSide = "net.minecraftforge.test.SimpleNetworkWrapperTest$CommonProxy", clientSide = "net.minecraftforge.test.SimpleNetworkWrapperTest$ClientProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper simpleNetworkWrapper;
    public static byte MESSAGE_BYTE = 35;
    public static class CommonProxy
    {
        public void init(FMLInitializationEvent event)
        {
            simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("TestChannel");
          simpleNetworkWrapper.registerMessageType(TestMessage.class, MESSAGE_BYTE); 
        }
    }

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void init(FMLInitializationEvent event)
        {
            super.init(event);
            simpleNetworkWrapper.registerMessageHandler(TestMessageClientHandler.class, TestMessage.class, Side.CLIENT);
        }
    }

    public static class ServerTickMessageSender
    {
        static int serverTickCount = 0;
      @SubscribeEvent
      public void onServerTick(ServerTickEvent event)
      {
        ++serverTickCount;
        if (serverTickCount % 100 == 0) {
          TestMessage message = new TestMessage(serverTickCount);
          simpleNetworkWrapper.sendToAll(message);  
        }
      }
        
    }
       
    public static class TestMessage implements IMessage
    {
        public TestMessage()
        {
        }
        public TestMessage(int i_count) 
        {
            count = i_count;
        }
        
        public int count;
        
        @Override
        public void fromBytes(ByteBuf buf)
        {
          count = buf.readInt();  
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
          buf.writeInt(count);  
        }
    }

    public static class TestMessageClientHandler implements IMessageHandler<TestMessage, IMessage>
    {
        @Override
        public IMessage onMessage(TestMessage message, MessageContext ctx)
        {
            System.out.println("TestMessage received on client, serverTickCount = " + message.count);
            return null;
        }
    }
    
}
