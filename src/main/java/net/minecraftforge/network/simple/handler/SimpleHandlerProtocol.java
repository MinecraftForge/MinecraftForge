/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple.handler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraftforge.network.simple.BaseProtocol;
import net.minecraftforge.network.simple.SimpleConnection;

import org.jetbrains.annotations.Nullable;

public interface SimpleHandlerProtocol<BUF extends FriendlyByteBuf, BASE> extends BaseProtocol<SimpleHandlerFlow<BUF, BASE>, SimpleHandlerProtocol<BUF, BASE>>, SimpleConnection<BASE> {
    /**
     * Creates a builder that validates both current protocol, and packet sending direction.
     * @param flow The direction that following packets are valid for. Null for bidirectional
     */
    SimpleHandlerFlow<BUF, BASE> flow(@Nullable PacketFlow flow);
}
