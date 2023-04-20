/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.holdersets;

import net.minecraft.core.HolderSet;

/**
 * Interface for mods' custom holderset types
 */
public interface ICustomHolderSet<T> extends HolderSet<T>
{
    /**
     * {@return HolderSetType registered to {@link ForgeRegistries.HOLDER_SET_TYPES}}
     */
    HolderSetType type();

    @Override
    default SerializationType serializationType()
    {
        return SerializationType.OBJECT;
    }
}
