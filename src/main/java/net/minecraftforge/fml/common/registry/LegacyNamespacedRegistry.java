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

package net.minecraftforge.fml.common.registry;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;

public class LegacyNamespacedRegistry<V> extends RegistryNamespaced<ResourceLocation, V>
{
    private Map<ResourceLocation, ResourceLocation> legacy_names = Maps.newHashMap();

    @Nullable
    public V getObject(@Nullable ResourceLocation name)
    {
        V ret = super.getObject(name);

        if (ret == null)
        {
            ResourceLocation new_name = legacy_names.get(name);
            if (new_name != null)
                ret = super.getObject(new_name);
        }

        return ret;
    }

    public void addLegacyName(ResourceLocation old_name, ResourceLocation new_name)
    {
        legacy_names.put(old_name, new_name);
    }
}
