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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Holds all required rules for {@link IResourceType}s that should be reloaded.
 */
@SideOnly(Side.CLIENT)
public interface ReloadRequirements
{
    /**
     * Checks if the given resource type should be reloaded. This is dependent on what was requested when a reload
     * was initiated.
     *
     * This returns true if no types were requested, as it can be assumed a full reload is required.
     *
     * @param type the type to check for
     * @return true if the given resource type should be reloaded
     */
    boolean shouldReload(IResourceType type);

    /**
     * Handles reload requirements with an inclusion list of {@link IResourceType} rather than an exclusion list.
     */
    class Include implements ReloadRequirements
    {
        private final Set<IResourceType> inclusion;

        public Include(Set<IResourceType> inclusion)
        {
            this.inclusion = inclusion;
        }

        @Override
        public boolean shouldReload(IResourceType type)
        {
            return this.inclusion.isEmpty() || this.inclusion.contains(type);
        }
    }

    /**
     * Handles reload requirements with an exclusion list of {@link IResourceType}.
     */
    class Exclude implements ReloadRequirements
    {
        private final Set<IResourceType> exclusion;

        public Exclude(Set<IResourceType> exclusion)
        {
            this.exclusion = exclusion;
        }

        @Override
        public boolean shouldReload(IResourceType type)
        {
            return !this.exclusion.contains(type);
        }
    }
}
