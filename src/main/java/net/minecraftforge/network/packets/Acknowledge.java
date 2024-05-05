/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/*
 * A simple packet used to acknowledge that the client has received and processed a message sent by the server.
 * The token can be anything, ideally it would an identifier sent from the server.
 */
public record Acknowledge(int token) {
    public static StreamCodec<FriendlyByteBuf, Acknowledge> STREAM_CODEC = StreamCodec.ofMember(Acknowledge::encode, Acknowledge::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(token);
    }

    public static Acknowledge decode(FriendlyByteBuf buf) {
        return new Acknowledge(buf.readVarInt());
    }
}