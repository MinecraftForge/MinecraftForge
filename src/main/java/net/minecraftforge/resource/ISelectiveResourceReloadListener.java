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

package net.minecraftforge.resource;

import java.util.function.Predicate;

import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public interface ISelectiveResourceReloadListener extends IResourceManagerReloadListener
{
    @Override
    default void onResourceManagerReload(IResourceManager resourceManager)
    {
        // For compatibility, call the selective version from the non-selective function
        onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
    }

    /**
     * A version of onResourceManager that selectively chooses {@link net.minecraftforge.resource.IResourceType}s
     * to reload.
     * When using this, the given predicate should be called to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager the resource manager being reloaded
     * @param resourcePredicate predicate to test whether any given resource type should be reloaded
     */
    void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate);
}
