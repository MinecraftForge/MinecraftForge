/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the type of an arbitrary value attached to registry entries. <br>
 * An attachment type is registered to the {@link net.minecraftforge.registries.ForgeRegistries#REGISTRY_ATTACHMENT_TYPES registry},
 * and may be used on different registries, depending on whether the type is {@linkplain #isValidFor(ResourceKey) valid} for a registry. <br>
 * <strong>Note:</strong> {@linkplain net.minecraftforge.registries.IForgeRegistry Forge registries} can have attachments but <i>only</i>
 * if they have a {@linkplain RegistryBuilder#supportsAttachments() wrapper}.
 *
 * @param <T> the type of the attached value
 * @see #builder(Codec)
 */
@ApiStatus.NonExtendable
public interface IRegistryAttachmentType<T>
{
    /**
     * {@return the default value which will be returned by queries on entries which do not have a value of that type attached}
     */
    @Nullable
    T getDefaultValue();

    /**
     * Checks if this attachment type can be used for the specified {@code registry}.
     *
     * @param registry the registry to check
     * @return if this type is valid for that registry
     */
    boolean isValidFor(ResourceKey<? extends Registry<?>> registry);

    /**
     * {@return the Codec which will be used to decode the attached values}
     */
    Codec<T> getCodec();

    /**
     * {@return the Codec which will be used for syncing the attachments to clients, or {@code null}, if this type shall not be synced to clients}
     */
    @Nullable Codec<T> getNetworkCodec();

    /**
     * Creates a builder for attachment types.
     *
     * @param codec the codec used to decode the attached values
     * @param <T>   the type of the attached values
     * @return the builder
     */
    static <T> RegistryAttachmentTypeBuilder<T> builder(Codec<T> codec)
    {
        return new RegistryAttachmentTypeBuilder<>(codec);
    }
}
