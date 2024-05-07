package net.minecraftforge.network.simple;

import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public interface IProtocol<FLOW extends IFlow, PRO extends IProtocol<FLOW, PRO>>
{
    PRO self();

    FLOW flow(@Nullable PacketFlow flow);

    /**
     * Consumer version of {@link #flow(PacketFlow)}. The Consumer will immediately be called with the created protocol.
     */
    default PRO flow(@Nullable PacketFlow flow, Consumer<FLOW> consumer) {
        consumer.accept(flow(flow));
        return this.self();
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Server to Client
     */
    default FLOW clientbound() {
        return flow(PacketFlow.CLIENTBOUND);
    }

    /**
     * Consumer version of {@link #clientbound()}. The Consumer will immediately be called with the created flow.
     */
    default PRO clientbound(Consumer<FLOW> consumer) {
        return flow(PacketFlow.CLIENTBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from Client to Server
     */
    default FLOW serverbound() {
        return flow(PacketFlow.SERVERBOUND);
    }

    /**
     * Consumer version of {@link #serverbound()}. The Consumer will immediately be called with the created flow.
     */
    default PRO serverbound(Consumer<FLOW> consumer) {
        return flow(PacketFlow.SERVERBOUND, consumer);
    }

    /**
     * Creates a builder that validates both current protocol, and that packets are send from either flow
     */
    default FLOW bidirectional() {
        return flow(null);
    }

    /**
     * Consumer version of {@link #bidirectional()}. The Consumer will immediately be called with the created flow.
     */
    default PRO bidirectional(Consumer<FLOW> consumer) {
        return flow(null, consumer);
    }
}
