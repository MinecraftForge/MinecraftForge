package cpw.mods.fml.common.network;

import io.netty.buffer.ByteBuf;

public interface IFMLMessage {
    IMessageHandler<? extends IFMLMessage> handler();
    void fromBytes(ByteBuf source);
    void toBytes(ByteBuf target);
}
