package net.minecraftforge.common.capabilities;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * An interface providing the ability for {@link ICapabilityProvider}s to
 * encode/decode capabilities over the network.
 */
public interface INetworkCapability {

    void encode(FriendlyByteBuf out, boolean writeAll);

    void decode(FriendlyByteBuf in);

    /**
     * Whether this capability's data is 'dirty' and needs to be sent to the client.
     */
    boolean requiresSync();

    default void encode(CompoundTag nbt, String key, boolean writeAll)
    {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        this.encode(buf, writeAll);
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        nbt.put(key, new ByteArrayTag(bytes));
    }

    default void decode(CompoundTag nbt, String key)
    {
        var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(nbt.getByteArray(key)));
        this.decode(buf);
        buf.release();
    }
}