/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/**
 * This event is fired on the {@linkplain net.minecraftforge.common.MinecraftForge#EVENT_BUS Forge event bus} before
 * values read from datapacks are attached to registry entries.
 */
public class AddBuiltInRegistryAttachmentsEvent extends Event
{
    private final Registry<?> registry;

    @ApiStatus.Internal
    public AddBuiltInRegistryAttachmentsEvent(Registry<?> registry)
    {
        this.registry = registry;
    }

    /**
     * {@return the registry}
     */
    public Registry<?> getRegistry()
    {
        return this.registry;
    }

    /**
     * {@return the key of the registry}
     */
    public ResourceKey<? extends Registry<?>> getKey()
    {
        return this.registry.key();
    }

    /**
     * Accepts the given registrar on the registry only if the key of the registry matches the {@code registryKey}.
     *
     * @param registryKey    the key of the registry to expect
     * @param attachmentType the type of the attachment
     * @param registrar      the registrar which will add the attachments
     * @param <R>            the type of the registry
     * @param <A>            the type of the attached values
     */
    @SuppressWarnings("unchecked")
    public <R, A> void register(ResourceKey<? extends Registry<R>> registryKey, ResourceKey<IRegistryAttachmentType<A>> attachmentType, BiConsumer<Registry<R>, IRegistryAttachmentHolder<R, A>> registrar)
    {
        if (registryKey == getKey())
        {
            final Registry<R> registry = (Registry<R>) getRegistry();
            final IRegistryAttachmentHolder<R, A> attachments = registry.attachment(attachmentType);
            if (attachments != null)
            {
                registrar.accept(registry, attachments);
            }
        }
    }
}