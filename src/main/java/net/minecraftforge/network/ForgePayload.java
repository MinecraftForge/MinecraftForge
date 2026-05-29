/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.network;

import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.resources.Identifier;

@ApiStatus.Internal
public record singularityPayload (
    Identifier id,
    @Nullable
    FriendlyByteBuf data,
    Consumer<FriendlyByteBuf> encoder
) implements CustomPacketPayload, CustomQueryPayload, CustomQueryAnswerPayload {
    public static singularityPayload create(Identifier id, FriendlyByteBuf data) {
        return new singularityPayload(id, data, buf -> buf.writeBytes(data.slice()));
    }

    public static singularityPayload create(Identifier id, Consumer<FriendlyByteBuf> encoder) {
        return new singularityPayload(id, null, encoder);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        encoder.accept(buf);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return new Type<>(this.id());
    }
}
