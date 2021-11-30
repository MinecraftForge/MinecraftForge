package net.minecraftforge.common.capabilities;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

/**
 * An interface providing the ability for {@link ICapabilityProvider}s to
 * encode/decode capabilities over the network.
 */
public interface INetworkCapability {

    /**
     * Write capabilities to the specified {@link FriendlyByteBuf}.
     * 
     * @param out      - the {@link FriendlyByteBuf} to write to
     * @param writeAll - {@code true} if all data should be written, {@code false}
     *                 if only dirty data should be written
     */
    void write(FriendlyByteBuf out, boolean writeAll);

    void read(FriendlyByteBuf in);

    /**
     * Whether this capability's data is 'dirty' and needs to be sent to the client.
     */
    boolean requiresSync();

    default byte[] write(boolean writeAll)
    {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        this.write(buf, writeAll);
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        return bytes;
    }

    default void readTag(byte[] bytes)
    {
        var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
        this.read(buf);
        buf.release();
    }
} 