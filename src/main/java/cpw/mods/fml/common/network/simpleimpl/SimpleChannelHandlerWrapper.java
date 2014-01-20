package cpw.mods.fml.common.network.simpleimpl;

import org.apache.logging.log4j.Level;
import net.minecraft.network.INetHandler;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleChannelHandlerWrapper<REQ extends IMessage, REPLY extends IMessage> extends SimpleChannelInboundHandler<REQ> {
    private IMessageHandler<REQ, REPLY> messageHandler;
    private Side side;
    public SimpleChannelHandlerWrapper(Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        try
        {
            messageHandler = handler.newInstance();
        } catch (Exception e)
        {
            Throwables.propagate(e);
        }
        this.side = side;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, REQ msg) throws Exception
    {
        INetHandler iNetHandler = ctx.attr(NetworkRegistry.NET_HANDLER).get();
        MessageContext context = new MessageContext(iNetHandler, side);
        REPLY result = messageHandler.onMessage(msg, context);
        if (result != null)
        {
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "SimpleChannelHandlerWrapper exception");
        super.exceptionCaught(ctx, cause);
    }
}
