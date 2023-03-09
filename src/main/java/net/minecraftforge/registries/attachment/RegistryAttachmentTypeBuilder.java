/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A builder for {@link IRegistryAttachmentType RegistryAttachmentTypes}.
 *
 * @param <T> the type of the attachment
 */
public class RegistryAttachmentTypeBuilder<T>
{
    private final Codec<T> codec;
    private @Nullable Codec<T> networkCodec;

    private @Nullable T defaultValue;
    private Predicate<ResourceKey<? extends Registry<?>>> registryTester = e -> false;

    RegistryAttachmentTypeBuilder(Codec<T> codec)
    {
        this.codec = codec;
    }

    /**
     * Sets the network codec used to sync attached values of this type.
     *
     * @param networkCodec the codec
     * @return the builder
     */
    public RegistryAttachmentTypeBuilder<T> withNetworkCodec(@Nullable Codec<T> networkCodec)
    {
        this.networkCodec = networkCodec;
        return this;
    }

    /**
     * Sets the default value of this type.
     *
     * @param defaultValue the default value
     * @return the builder
     */
    public RegistryAttachmentTypeBuilder<T> withDefaultValue(@Nullable T defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Allows the type to be used on the specified {@code registry}.
     *
     * @param registry the registry to allow this type to be used on
     * @return the builder
     */
    public RegistryAttachmentTypeBuilder<T> attachOnRegistry(ResourceKey<? extends Registry<?>> registry)
    {
        this.registryTester = this.registryTester.or(e -> e == registry);
        return this;
    }

    /**
     * Sets a predicate which tests if this type can be used on the registry.
     *
     * @param registryTester the tester
     * @return the builder
     */
    public RegistryAttachmentTypeBuilder<T> withValidFor(Predicate<ResourceKey<? extends Registry<?>>> registryTester)
    {
        this.registryTester = registryTester;
        return this;
    }

    /**
     * Builds the type.
     *
     * @return the built type
     */
    public IRegistryAttachmentType<T> build()
    {
        return new IRegistryAttachmentType<>()
        {
            @Override
            public @Nullable T getDefaultValue()
            {
                return defaultValue;
            }

            @Override
            public boolean isValidFor(ResourceKey<? extends Registry<?>> registry)
            {
                return registryTester.test(registry);
            }

            @Override
            public Codec<T> getCodec()
            {
                return codec;
            }

            @Override
            public @Nullable Codec<T> getNetworkCodec()
            {
                return networkCodec;
            }
        };
    }
}
