/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record LoginWrapper(ResourceLocation channel, FriendlyByteBuf data) {
    public static LoginWrapper decode(FriendlyByteBuf buf) {
        var channel = buf.readResourceLocation();
        var len = buf.readVarInt();
        var data = new FriendlyByteBuf(buf.readBytes(len));
        return new LoginWrapper(channel, data);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(channel);
        buf.writeVarInt(data.readableBytes());
        buf.writeBytes(data.slice());
    }
}
