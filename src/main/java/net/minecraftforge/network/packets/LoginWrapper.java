/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel;

public class LoginWrapper {
    public static final StreamCodec<FriendlyByteBuf, LoginWrapper> STREAM_CODEC = StreamCodec.ofMember(LoginWrapper::encode, LoginWrapper::new);
    private final ResourceLocation name;
    private FriendlyByteBuf data;
    private final Channel<Object> channel;
    private final Object packet;

    public <MSG> LoginWrapper(Channel<MSG> channel, MSG packet) {
        this(channel.getName(), null, channel, packet);
    }

    private LoginWrapper(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.wrap(buf.readBytes(buf.readVarInt())), null, null);
    }

    @SuppressWarnings("unchecked")
    private LoginWrapper(ResourceLocation name, FriendlyByteBuf data, Channel<?> channel, Object packet) {
        this.name = name;
        this.data = data;
        this.channel = (Channel<Object>)channel;
        this.packet = packet;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(name);

        if (data == null) {
            data = buf.wrap(Unpooled.buffer());
            ((Channel<Object>)channel).encode(data, packet);
        }

        buf.writeVarInt(data.readableBytes());
        buf.writeBytes(data.slice());
    }

    public ResourceLocation name() {
        return this.name;
    }

    public FriendlyByteBuf data() {
        return this.data;
    }
}
