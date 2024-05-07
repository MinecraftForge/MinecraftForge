/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.simple;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

public interface SimpleProtocol<BUF extends FriendlyByteBuf, BASE> extends IProtocol<SimpleFlow<BUF, BASE>, SimpleProtocol<BUF, BASE>>, SimpleConnection<Object> {
    /**
     * Creates a builder that validates both current protocol, and packet sending direction.
     * @param flow The direction that following packets are valid for. Null for bidirectional
     */
    SimpleFlow<BUF, BASE> flow(@Nullable PacketFlow flow);

    @Override
    default SimpleProtocol<BUF, BASE> self() {
        return this;
    }
}
