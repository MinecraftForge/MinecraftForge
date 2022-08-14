/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.network.protocol.Packet;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ICustomPacket<T extends Packet<?>> {
    /**
     * Returns a unsafe reference to this packet's internal data.
     * Any modifications to this buffer will be reflected in the main buffer.
     */
    @Nullable
    FriendlyByteBuf getInternalData();

    ResourceLocation getName();

    int getIndex();

    default NetworkDirection getDirection() {
        return NetworkDirection.directionFor(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default T getThis() {
        return (T)this;
    }
}
