/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Entry point for a {@link net.minecraftforge.network.SimpleChannel SimpleChannel} protocol without any extra context handler marshaling.
 */
public interface SimpleProtocol<BUF extends FriendlyByteBuf, BASE> extends BaseProtocol<SimpleFlow<BUF, BASE>, SimpleProtocol<BUF, BASE>>, SimpleConnection<BASE> {
}
