/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

public class ReverseTagWrapper<T>
{
    private final T target;
    private final IntSupplier genSupplier;
    private final Supplier<TagCollection<T>> colSupplier;

    private int generation = -1;
    private Set<ResourceLocation> cache = null;

    public ReverseTagWrapper(T target, IntSupplier genSupplier, Supplier<TagCollection<T>> colSupplier)
    {
        this.target = target;
        this.genSupplier = genSupplier;
        this.colSupplier = colSupplier;
    }

    public Set<ResourceLocation> getTagNames()
    {
        if (cache == null || generation != genSupplier.getAsInt())
        {
            this.cache = Collections.unmodifiableSet(new HashSet<>(colSupplier.get().getOwningTags(target)));
            this.generation = genSupplier.getAsInt();
        }
        return this.cache;
    }
}
