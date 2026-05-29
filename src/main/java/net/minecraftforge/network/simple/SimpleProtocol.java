/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.simple;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Entry point for a {@link net.minecraftsingularity.network.SimpleChannel SimpleChannel} protocol without any extra context handler marshaling.
 */
public interface SimpleProtocol<BUF extends FriendlyByteBuf, BASE> extends BaseProtocol<SimpleFlow<BUF, BASE>, SimpleProtocol<BUF, BASE>>, SimpleConnection<BASE> {
}
