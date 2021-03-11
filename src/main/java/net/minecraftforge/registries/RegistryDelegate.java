/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.registries;

import com.google.common.base.Objects;

import net.minecraft.util.ResourceLocation;

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
