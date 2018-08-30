package net.minecraftforge.fml.network;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.UnsafeHacks;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ICustomPacket<T extends Packet<?>> {
    enum Fields {
        CPACKETCUSTOMPAYLOAD(CPacketCustomPayload.class),
        SPACKETCUSTOMPAYLOAD(SPacketCustomPayload.class),
        ;

        static final Reference2ReferenceArrayMap<Class<?>, Fields> lookup;
        static {
            lookup = Stream.of(values()).
                    collect(Collectors.toMap(Fields::getClazz, Function.identity(), (m1, m2)->m1, Reference2ReferenceArrayMap::new));
        }

        private final Class<?> clazz;

        public final Field data;
        public final Field channel;
        Fields(Class<?> customPacketClass)
        {
            this.clazz = customPacketClass;
            try
            {
                data = customPacketClass.getDeclaredField("data");
                channel = customPacketClass.getDeclaredField("channel");
            }
            catch (NoSuchFieldException e)
            {
                throw new RuntimeException("BARF?");
            }
        }

        private Class<?> getClazz()
        {
            return clazz;
        }
    }

    default PacketBuffer getData() {
        return UnsafeHacks.getField(Fields.lookup.get(this.getClass()).data, this);
    }

    default ResourceLocation getName() {
        return UnsafeHacks.getField(Fields.lookup.get(this.getClass()).channel, this);
    }

    default NetworkDirection getDirection() {
        return NetworkDirection.directionFor(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default T getThis() {
        return (T)this;
    }
}
