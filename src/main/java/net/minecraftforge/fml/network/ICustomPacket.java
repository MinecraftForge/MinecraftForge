package net.minecraftforge.fml.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public interface ICustomPacket<T extends Packet<?>> {
    default PacketBuffer getData() {
        return getBufferDataSupplier().get();
    }

    default ResourceLocation getName() {
        return getChannelNameSupplier().get();
    }

    default NetworkDirection getDirection() {
        return NetworkDirection.directionFor(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default T getThis() {
        return (T)this;
    }

    Supplier<ResourceLocation> getChannelNameSupplier();
    Supplier<PacketBuffer> getBufferDataSupplier();
}
