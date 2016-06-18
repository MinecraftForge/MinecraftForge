/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common.registry;

import io.netty.buffer.ByteBuf;

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
    public void writeSpawnData(ByteBuf buffer);

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    public void readSpawnData(ByteBuf additionalData);
}
