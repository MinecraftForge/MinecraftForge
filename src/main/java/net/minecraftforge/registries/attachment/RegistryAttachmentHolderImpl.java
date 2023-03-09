/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class RegistryAttachmentHolderImpl<R, A> implements IRegistryAttachmentHolderInternal<R, A>
{
    private final IRegistryAttachmentType<A> type;
    private final MappedRegistry<R> registry;

    private Map<Holder<R>, A> attachments = new HashMap<>();
    private Map<Holder<R>, A> view = Collections.unmodifiableMap(attachments);

    private boolean isFrozen = true;

    public RegistryAttachmentHolderImpl(IRegistryAttachmentType<A> type, MappedRegistry<R> registry)
    {
        this.type = type;
        this.registry = registry;
    }

    @Override
    public @Nullable A get(R entry)
    {
        return get(registry.wrapAsHolder(entry));
    }

    @Override
    public @Nullable A get(Holder<R> entry)
    {
        return attachments.getOrDefault(entry, type.getDefaultValue());
    }

    @Override
    public void attach(R entry, A value)
    {
        attach(registry.wrapAsHolder(entry), value);
    }

    @Override
    public void attach(Holder<R> entry, A value)
    {
        if (isFrozen)
            throw new UnsupportedOperationException("Cannot add attachments to frozen attachment holders!");
        this.attachments.put(entry, value);
    }

    @Override
    public void replaceWith(Map<Holder<R>, A> map)
    {
        this.attachments = map;
        this.view = Collections.unmodifiableMap(attachments);
    }

    @Override
    public Map<Holder<R>, A> view()
    {
        return view;
    }

    @Override
    public void freeze()
    {
        this.isFrozen = true;
    }

    @Override
    public void unfreeze()
    {
        this.isFrozen = false;
    }
}
