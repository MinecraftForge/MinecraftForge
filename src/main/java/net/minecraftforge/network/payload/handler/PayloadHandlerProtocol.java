/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network.payload.handler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftsingularity.network.payload.PayloadConnection;
import net.minecraftsingularity.network.simple.BaseProtocol;

/**
 * Entry point for a {@link net.minecraftsingularity.network.PayloadChannel PayloadChannel} protocol that mimics vanilla's
 * {@link net.minecraft.network.protocol.Packet Packet} system where every packet must implement
 * {@link SimplePacket} and will have their {@link SimplePacket#handle(Object, net.minecraftsingularity.event.network.CustomPayloadEvent.Context) handle(CTX, Context)}
 * method called.
 */
public interface PayloadHandlerProtocol<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload> extends BaseProtocol<PayloadHandlerFlow<BUF, BASE>, PayloadHandlerProtocol<BUF, BASE>>, PayloadConnection<BASE> {
}
