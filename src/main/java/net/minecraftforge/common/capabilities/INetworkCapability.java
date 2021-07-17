package net.minecraftforge.common.capabilities;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

/**
 * An interface providing the ability for {@link ICapabilityProvider}s to
 * encode/decode capabilities over the network.
 */
public interface INetworkCapability {

    String NBT_KEY = "__FORGE_CAPABILITY__";

    void encode(PacketBuffer out, boolean writeAll);

    void decode(PacketBuffer in);

    /**
     * Whether this capability's data is 'dirty' and needs to be sent to the client.
     */
    boolean requiresSync();

    default void encodeNbt(CompoundNBT nbt, boolean writeAll)
    {
        PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
        this.encode(capabilityData, writeAll);
        byte[] capabilityDataBytes = new byte[capabilityData.readableBytes()];
        capabilityData.readBytes(capabilityDataBytes);
        nbt.put(NBT_KEY, new ByteArrayNBT(capabilityDataBytes));
    }

    default void decodeNbt(CompoundNBT nbt)
    {
        this.decode(new PacketBuffer(Unpooled.wrappedBuffer(nbt.getByteArray(NBT_KEY))));
    }
}
