package cpw.mods.fml.common.network.simpleimpl;


/**
 * A message handler based on {@link IMessage}. Implement and override {@link #onMessage(IMessage)} to
 * process your packet. Supply the class to {@link SimpleNetworkWrapper#registerMessage(Class, Class, byte, cpw.mods.fml.relauncher.Side)}
 * to register both the message type and it's associated handler.
 *
 * @author cpw
 *
 * @param <REQ> This is the request type - it is the message you expect to <em>receive</em> from remote.
 * @param <REPLY> This is the reply type - it is the message you expect to <em>send</em> in reply. You can use IMessage as the type here
 * if you don't anticipate sending a reply.
 */
public interface IMessageHandler<REQ extends IMessage, REPLY extends IMessage> {
    /**
     * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
     * is needed.
     *
     * @param message The message
     * @return an optional return message
     */
    public REPLY onMessage(REQ message, MessageContext ctx);
}