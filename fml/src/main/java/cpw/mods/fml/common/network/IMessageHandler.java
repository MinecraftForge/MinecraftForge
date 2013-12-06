package cpw.mods.fml.common.network;

import cpw.mods.fml.common.network.handshake.NetworkDispatcher;


public interface IMessageHandler<M extends IFMLMessage> {
    /**
     * Unsigned byte for the discriminator
     * @return an unsigned byte
     */
    short discriminator();
    Class<? extends M> messageType(short discriminator);
    void receiveMessage(M message, NetworkDispatcher dispatcher);
}
