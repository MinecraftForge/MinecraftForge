/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.network.syncher.EntityDataSerializer;

public final class DataSerializerEntry extends ForgeRegistryEntry<DataSerializerEntry>
{
    private final EntityDataSerializer<?> serializer;

    public DataSerializerEntry(EntityDataSerializer<?> serializer)
    {
        this.serializer = serializer;
    }

    public EntityDataSerializer<?> getSerializer()
    {
        return serializer;
    }
}
