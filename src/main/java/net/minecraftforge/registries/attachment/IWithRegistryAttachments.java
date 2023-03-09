/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * An interface implemented by {@link net.minecraft.core.Registry} and {@link net.minecraftforge.registries.IForgeRegistry}
 * which is used to query {@link IRegistryAttachmentHolder attachment holders}.
 *
 * @param <T> the type of the registry entries
 */
public interface IWithRegistryAttachments<T>
{
    /**
     * Gets an attachment holder for a specified type.
     *
     * @param type the type of the attachment
     * @param <A>  the type of the attached values
     * @return the holder, or {@code null} if the attachment type cannot be used by this registry
     */
    @Nullable
    default <A> IRegistryAttachmentHolder<T, A> attachment(ResourceKey<IRegistryAttachmentType<A>> type)
    {
        return null;
    }

    /**
     * {@return a view of all existing attachment holders on this registry}
     */
    default Map<ResourceKey<IRegistryAttachmentType<?>>, IRegistryAttachmentHolder<T, ?>> attachments()
    {
        return Map.of();
    }

    @ApiStatus.Internal
    default void setDuringAttachmentLoading(boolean duringAttachmentLoading)
    {
    }
}
