/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple.handler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.simple.BaseProtocol;
import net.minecraftforge.network.simple.SimpleConnection;

/**
 * Entry point for a {@link net.minecraftforge.network.SimpleChannel SimpleChannel} protocol that mimics vanilla's
 * {@link net.minecraft.network.protocol.Packet Packet} system where every packet must implement
 * {@link SimplePacket} and will have their {@link SimplePacket#handle(Object, net.minecraftforge.event.network.CustomPayloadEvent.Context) handle(CTX, Context)}
 * method called.
 */
public interface SimpleHandlerProtocol<BUF extends FriendlyByteBuf, BASE> extends BaseProtocol<SimpleHandlerFlow<BUF, BASE>, SimpleHandlerProtocol<BUF, BASE>>, SimpleConnection<BASE> {
}
