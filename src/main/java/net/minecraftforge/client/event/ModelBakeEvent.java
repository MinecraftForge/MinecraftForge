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

package net.minecraftforge.client.event;

import java.util.Map;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

/**
 * Fired when the ModelManager is notified of the resource manager reloading.
 * Called after model registry is setup, but before it's passed to BlockModelShapes.
 */
// TODO: try to merge with ICustomModelLoader
public class ModelBakeEvent extends Event implements IModBusEvent
{
    private final ModelManager modelManager;
    private final Map<ResourceLocation, IBakedModel> modelRegistry;
    private final ModelLoader modelLoader;

    public ModelBakeEvent(ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader)
    {
        this.modelManager = modelManager;
        this.modelRegistry = modelRegistry;
        this.modelLoader = modelLoader;
    }

    public ModelManager getModelManager()
    {
        return modelManager;
    }

    public Map<ResourceLocation, IBakedModel> getModelRegistry()
    {
        return modelRegistry;
    }

    public ModelLoader getModelLoader()
    {
        return modelLoader;
    }
}
