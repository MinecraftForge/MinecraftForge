/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple.handler;

import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Interface that all packets must implement if using the
 * {@link net.minecraftforge.network.SimpleChannel#protocol(io.netty.util.AttributeKey,net.minecraftforge.network.NetworkProtocol) SimpleChannel.protocol(AttributeKey, NetworkProtocol)}
 *  function.
 *
 * @param <C> The type of the context object
 */
public interface SimplePacket<C> {
    boolean handle(C handler, CustomPayloadEvent.Context event);
}
