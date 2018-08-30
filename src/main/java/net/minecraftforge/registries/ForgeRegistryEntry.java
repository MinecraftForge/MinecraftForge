/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
import net.minecraftforge.fml.ModThreadContext;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class ForgeRegistryEntry<V extends ForgeRegistryEntry<V>>
{
    private final TypeToken<V> token = new TypeToken<V>(getClass()){};
    public final IRegistryDelegate<V> delegate = new RegistryDelegate<>((V)this, (Class<V>)token.getRawType());
    private ResourceLocation registryName = null;

    public final V setRegistryName(String name)
    {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase();
        name = index == -1 ? name : name.substring(index + 1);
        String prefix = ModThreadContext.get().getActiveContainer().getPrefix();
        if (!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
        {
            LogManager.getLogger().info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
            prefix = oldPrefix;
        }
        this.registryName = new ResourceLocation(prefix, name);
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