/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.entity;

import net.minecraft.network.FriendlyByteBuf;

/**
 * A interface for Entities that need extra information to be communicated
 * between the server and client when they are spawned.
 */
public interface IEntityAdditionalSpawnData
{
    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    void writeSpawnData(FriendlyByteBuf buffer);

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    void readSpawnData(FriendlyByteBuf additionalData);
}
