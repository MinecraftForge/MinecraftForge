/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.google.common.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

/**
 * Default implementation of IForgeRegistryEntry, this is necessary to reduce redundant code.
 * This also enables the registrie's ability to manage delegates. Which are automatically updated
 * if another entry overrides existing ones in the registry.
 */
@SuppressWarnings("unchecked")
public abstract class ForgeRegistryEntry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryEntry<V>
{
    @SuppressWarnings("serial")
    private final TypeToken<V> token = new TypeToken<V>(getClass()){};
    public final IRegistryDelegate<V> delegate = new RegistryDelegate<>((V)this, (Class<V>)token.getRawType());
    private ResourceLocation registryName = null;

    public final V setRegistryName(String name)
    {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name, true);
        return (V)this;
    }

    //Helper functions
    public final V setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }

    public final V setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Nullable
    public final ResourceLocation getRegistryName()
    {
        if (delegate.name() != null) return delegate.name();
        return registryName != null ? registryName : null;
    }

    public final Class<V> getRegistryType() { return (Class<V>)token.getRawType(); }
}
