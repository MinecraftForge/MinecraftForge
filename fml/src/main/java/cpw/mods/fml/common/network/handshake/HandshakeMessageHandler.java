package cpw.mods.fml.common.network.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class HandshakeMessageHandler<S extends Enum<S> & IHandshakeState<S>> extends SimpleChannelInboundHandler<FMLHandshakeMessage> {
    private static final AttributeKey<IHandshakeState<?>> STATE = new AttributeKey<IHandshakeState<?>>("fml:handshake-state");
    private final AttributeKey<S> fmlHandshakeState;
    private S initialState;

    @SuppressWarnings("unchecked")
    public HandshakeMessageHandler(Class<S> stateType)
    {
        fmlHandshakeState = (AttributeKey<S>) STATE;
        initialState = Enum.valueOf(stateType, "START");
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FMLHandshakeMessage msg) throws Exception
    {
        S state = ctx.attr(fmlHandshakeState).get();
        S newState = state.accept(ctx, msg);
        ctx.attr(fmlHandshakeState).set(newState);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.attr(fmlHandshakeState).set(initialState);
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        S state = ctx.attr(fmlHandshakeState).get();
        S newState = state.accept(ctx, null);
        ctx.attr(fmlHandshakeState).set(newState);
    }
}
