/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Interface that all packets must implement if using the {@link net.minecraftforge.network.SimpleChannel#handler(io.netty.util.AttributeKey)} function.
 *
 * @param <C> The type of the context object
 */
public interface SimplePacket<C> {
    boolean handle(C handler, CustomPayloadEvent.Context event);
}
