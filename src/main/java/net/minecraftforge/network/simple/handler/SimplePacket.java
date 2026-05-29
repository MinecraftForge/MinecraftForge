/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.simple.handler;

import net.minecraftsingularity.event.network.CustomPayloadEvent;

/**
 * Interface that all packets must implement if using the
 * {@link net.minecraftsingularity.network.SimpleChannel#protocol(io.netty.util.AttributeKey,net.minecraftsingularity.network.NetworkProtocol) SimpleChannel.protocol(AttributeKey, NetworkProtocol)}
 *  function.
 *
 * @param <C> The type of the context object
 */
public interface SimplePacket<C> {
    boolean handle(C handler, CustomPayloadEvent.Context event);
}
