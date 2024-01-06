/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import com.google.common.base.Objects;

import net.minecraft.resources.ResourceLocation;

/*
 * This is the internal implementation class of the delegate.
 */
final class RegistryDelegate<T> implements IRegistryDelegate<T>
{
    private T referent;
    private ResourceLocation name;
    private final Class<T> type;

    public RegistryDelegate(T referent, Class<T> type)
    {
        this.referent = referent;
        this.type = type;
    }

    @Override
    public T get() { return referent; }
    @Override
    public ResourceLocation name() { return name; }
    @Override
    public Class<T> type() { return this.type; }

    void changeReference(T newTarget){ this.referent = newTarget; }
    public void setName(ResourceLocation name) { this.name = name; }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof RegistryDelegate)
        {
            RegistryDelegate<?> other = (RegistryDelegate<?>) obj;
            return Objects.equal(other.name, name);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name);
    }
}
