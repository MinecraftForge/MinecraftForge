/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.network.datasync.IDataSerializer;

public final class DataSerializerEntry extends ForgeRegistryEntry<DataSerializerEntry>
{
    private final IDataSerializer<?> serializer;

    public DataSerializerEntry(IDataSerializer<?> serializer)
    {
        this.serializer = serializer;
    }

    public IDataSerializer<?> getSerializer()
    {
        return serializer;
    }
}
