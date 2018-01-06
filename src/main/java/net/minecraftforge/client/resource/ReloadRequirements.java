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

package net.minecraftforge.client.resource;

import java.util.Set;
import java.util.function.Predicate;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Sets;

/**
 * Holds methods to create standard predicates to select {@link IResourceType}s that should be reloaded.
 */
@SideOnly(Side.CLIENT)
public final class ReloadRequirements
{
    /**
     * Creates a reload predicate accepting all resource types.
     *
     * @return a predicate accepting all types
     */
    public static Predicate<IResourceType> all()
    {
        return type -> true;
    }

    /**
     * Creates an inclusive reload predicate. Only given resource types will be loaded along with this.
     * If the inclusion set is empty, all types will be accepted.
     *
     * @param inclusion the set of resource types to be included in the reload
     * @return an inclusion predicate based on the given types
     */
    public static Predicate<IResourceType> include(IResourceType... inclusion)
    {
        if (inclusion.length == 0)
        {
            return all();
        }

        Set<IResourceType> inclusionSet = Sets.newHashSet(inclusion);
        return inclusionSet::contains;
    }

    /**
     * Creates an exclusive reload predicate. Only resource types not given will be loaded along with this.
     *
     * @param exclusion the set of resource types to be excluded from the reload
     * @return an exclusion predicate based on the given types
     */
    public static Predicate<IResourceType> exclude(IResourceType... exclusion)
    {
        if (exclusion.length == 0)
        {
            return all();
        }

        return include(exclusion).negate();
    }
}
