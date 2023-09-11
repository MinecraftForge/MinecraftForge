/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import net.minecraft.network.FriendlyByteBuf;

public record ConfigData(String name, byte[] data) {
    public void encode(final FriendlyByteBuf buf) {
        buf.writeUtf(this.name());
        buf.writeByteArray(this.data());
    }

    public static ConfigData decode(FriendlyByteBuf buf) {
        return new ConfigData(buf.readUtf(), buf.readByteArray());
    }
}