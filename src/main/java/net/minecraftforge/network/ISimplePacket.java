/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.network;

import net.minecraftforge.event.network.CustomPayloadEvent;

public interface ISimplePacket<C> {
    boolean handle(C handler, CustomPayloadEvent.Context event);
}
