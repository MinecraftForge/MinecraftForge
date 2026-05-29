/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftsingularity.network.simple.BaseProtocol;

/**
 * Entry point for a {@link net.minecraftsingularity.network.PayloadChannel PayloadChannel} protocol without any extra context handler marshaling.
 */
public interface PayloadProtocol<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload> extends BaseProtocol<PayloadFlow<BUF, BASE>, PayloadProtocol<BUF, BASE>>, PayloadConnection<BASE> {
}
