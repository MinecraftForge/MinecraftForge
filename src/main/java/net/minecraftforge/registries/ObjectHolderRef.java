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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import jline.internal.Nullable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

/**
 * Used in tracking {@link ObjectHolder} references.
 */
@SuppressWarnings("rawtypes")
interface ObjectHolderRef
{
    /**
     * Updates the referenced object holder.
     * Could be setting a field or invalidating some cache.
     */
    void apply();

    /**
     * @return whether the reference is valid (the underlying registry exists etc.)
     */
    boolean isValid();

    /**
     * Helper method for getting the registry associated with a type.
     *
     * @param referenceType the type to check
     * @return the associated registry if the type is indeed registerable, null otherwise
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static ForgeRegistry<?> getRegistryForType(Class<?> referenceType) {
        Queue<Class<?>> typesToExamine = new LinkedList<>();
        typesToExamine.add(referenceType);

        ForgeRegistry<?> registry = null;
        while (!typesToExamine.isEmpty() && registry == null)
        {
            Class<?> type = typesToExamine.remove();
            Collections.addAll(typesToExamine, type.getInterfaces());
            if (IForgeRegistryEntry.class.isAssignableFrom(type))
            {
                registry = (ForgeRegistry<?>) GameRegistry.findRegistry((Class<IForgeRegistryEntry>) type);
                final Class<?> parentType = type.getSuperclass();
                if (parentType != null)
                {
                    typesToExamine.add(parentType);
                }
            }
        }
        return registry;
    }
}
