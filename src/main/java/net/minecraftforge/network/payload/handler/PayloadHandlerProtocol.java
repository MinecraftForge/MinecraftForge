/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.payload.handler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.network.payload.PayloadConnection;
import net.minecraftforge.network.simple.BaseProtocol;

/**
 * Entry point for a {@link net.minecraftforge.network.PayloadChannel PayloadChannel} protocol that mimics vanilla's
 * {@link net.minecraft.network.protocol.Packet Packet} system where every packet must implement
 * {@link SimplePacket} and will have their {@link SimplePacket#handle(Object, net.minecraftforge.event.network.CustomPayloadEvent.Context) handle(CTX, Context)}
 * method called.
 */
public interface PayloadHandlerProtocol<BUF extends FriendlyByteBuf, BASE extends CustomPacketPayload> extends BaseProtocol<PayloadHandlerFlow<BUF, BASE>, PayloadHandlerProtocol<BUF, BASE>>, PayloadConnection<BASE> {
}
