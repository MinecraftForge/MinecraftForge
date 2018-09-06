/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.client.model;

import java.util.function.Predicate;

import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public interface ICustomModelLoader extends ISelectiveResourceReloadListener
{
    @Override
    void func_195410_a(IResourceManager resourceManager);

    @Override
    default void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate)
    {
        if (resourcePredicate.test(VanillaResourceType.MODELS))
        {
            func_195410_a(resourceManager);
        }
    }

    /*
     * Checks if given model should be loaded by this loader.
     * Reading file contents is inadvisable, if possible decision should be made based on the location alone.
     */
    boolean accepts(ResourceLocation modelLocation);

    /*
     * loads (or reloads) specified model
     */
    IModel loadModel(ResourceLocation modelLocation) throws Exception;
}
