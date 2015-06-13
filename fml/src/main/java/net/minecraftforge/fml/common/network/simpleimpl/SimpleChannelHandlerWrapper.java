package net.minecraftforge.fml.common.network.simpleimpl;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Level;

import net.minecraft.network.INetHandler;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Preconditions;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleChannelHandlerWrapper<REQ extends IMessage, REPLY extends IMessage> extends SimpleChannelInboundHandler<REQ> {
    final IMessageHandler<? super REQ, ? extends REPLY> messageHandler;
    final Side side;
    
    @Deprecated
    public SimpleChannelHandlerWrapper(Class<? extends IMessageHandler<? super REQ, ? extends REPLY>> handler, Side side, Class<REQ> requestType)
    {
        this(SimpleNetworkWrapper.instantiate(handler), side, requestType);
    }
    
    public SimpleChannelHandlerWrapper(IMessageHandler<? super REQ, ? extends REPLY> handler, Side side, Class<REQ> requestType)
    {
        super(requestType);
        messageHandler = Preconditions.checkNotNull(handler, "IMessageHandler must not be null");
        this.side = side;
    }
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, REQ msg) throws Exception
    {
        INetHandler iNetHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        MessageContext context = new MessageContext(iNetHandler, side);
        final REPLY result = messageHandler.onMessage(msg, context);
        if (result != null)
        {
            // need to do this on the main thread, otherwise someone might change FML_MESSAGETARGET in between...
            FMLCommonHandler.instance().getWorldThread(iNetHandler).addScheduledTask(new Runnable() {
                
                @Override
                public void run()
                {
                    sendReply(ctx, result);
                }
            });
            
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "SimpleChannelHandlerWrapper exception");
        super.exceptionCaught(ctx, cause);
    }
    
    static void sendReply(final ChannelHandlerContext ctx, final IMessage reply)
    {
        ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.REPLY);
        ctx.writeAndFlush(reply).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    final static class NonAsyncWrapper<REQ extends IMessage, REPLY extends IMessage> extends SimpleChannelHandlerWrapper<REQ, REPLY> {
        
        NonAsyncWrapper(IMessageHandler<? super REQ, ? extends REPLY> handler, Side side, Class<REQ> requestType)
        {
            super(handler, side, requestType);
        }
        
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final REQ msg) throws Exception
        {
            INetHandler iNetHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            final MessageContext context = new MessageContext(iNetHandler, side);
            IThreadListener scheduler = FMLCommonHandler.instance().getWorldThread(iNetHandler);
            
            scheduler.addScheduledTask(new Runnable() {
                
                @Override
                public void run()
                {
                    REPLY result = messageHandler.onMessage(msg, context);
                    if (result != null)
                    {
                        sendReply(ctx, result);
                    }
                }
            });
        }
        
    }
}
