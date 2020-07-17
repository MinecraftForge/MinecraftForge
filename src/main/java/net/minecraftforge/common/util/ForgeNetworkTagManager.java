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

package net.minecraftforge.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.tags.ITag;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

@Deprecated //For Forge's internal use only.
public class ForgeNetworkTagManager extends NetworkTagManager
{
    private static Map<RegistryKey<?>, Set<ResourceLocation>> defaults = new HashMap<>();

    /**
     * Returns a new instance of this tag manager, again, only exposed for Forge's Hook use.
     */
    public static NetworkTagManager create()
    {
        return new ForgeNetworkTagManager();
    }

    /**
     * This will provide default tags if the server side does not send us one.
     * Note: If they DO send us a empty one, this DOES NOT override that.
     * This is 100% intended to be only used by Forge tto make sure our tags behave correctly on vanilla servers for the edge cases where we use tags for in-game loogic.
     * Notably: Furnace burnables.
     */
    public static <T> ITag.INamedTag<T> defaulted(RegistryKey<Registry<T>> type, ITag.INamedTag<T> tag, Set<ResourceLocation> values)
    {
        defaults.put(RegistryKey.func_240903_a_(type, tag.func_230234_a_()), ImmutableSet.copyOf(values));
        return tag;
    }

    /**
     * Copies the resource key from the parent tag to a new one.
     * This simplifies the item variants of block tags.
     * WILL explode if the parent hasn't been defaulted.
     */
    public static <T> ITag.INamedTag<T> defaulted(RegistryKey<Registry<T>> type, ITag.INamedTag<T> tag, RegistryKey<?> parent)
    {
        RegistryKey<T> regKey = RegistryKey.func_240903_a_(type, tag.func_230234_a_());
        Set<ResourceLocation> other = defaults.get(parent);
        if (other == null)
            throw new IllegalStateException("Attempted to set nested default without setting parent: " + regKey + " -> " + parent);
        defaults.put(regKey, other);
        return tag;
    }

    private ForgeNetworkTagManager()
    {
        this.blocks = new ForgeNetworkTagCollection<>(Registry.BLOCK, "tags/blocks", "block");
        this.items = new ForgeNetworkTagCollection<>(Registry.ITEM, "tags/items", "item");
        this.fluids = new ForgeNetworkTagCollection<>(Registry.FLUID, "tags/fluids", "fluid");
        this.entityTypes = new ForgeNetworkTagCollection<>(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");
    }

    private static class ForgeNetworkTagCollection<T> extends NetworkTagCollection<T>
    {
        private final ITag<T> EMPTY = Tag.func_241284_a_();
        private final Registry<T> registry;
        private final Map<ResourceLocation, ITag<T>> cache = new HashMap<>();

        public ForgeNetworkTagCollection(Registry<T> registry, String path, String name)
        {
            super(registry, path, name);
            this.registry = registry;
        }

        @Override
        @Nullable
        public ITag<T> get(ResourceLocation key)
        {
            ITag<T> ret = super.get(key);
            if (ret == null)
            {
                ret = cache.get(key);
                if (ret == null) {
                    RegistryKey<T> regKey = RegistryKey.func_240903_a_(this.registry.getRegistryKey(), key);
                    Set<ResourceLocation> defaults = ForgeNetworkTagManager.defaults.get(regKey);
                    if (defaults == null)
                    {
                        ret = EMPTY;
                    }
                    else
                    {
                        Set<T> values = defaults.stream().map(getEntryLookup()).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
                        ret = Tag.func_241286_a_(values);
                    }
                    this.cache.put(key, ret);
                }
            }

            return ret;
        }
    }

}
