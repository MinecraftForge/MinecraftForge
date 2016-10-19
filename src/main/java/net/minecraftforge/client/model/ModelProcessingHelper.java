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

package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;

public class ModelProcessingHelper
{
    public static IModel retexture(IModel model, ImmutableMap<String, String> textures)
    {
        if(model instanceof IRetexturableModel)
        {
            model = ((IRetexturableModel)model).retexture(textures);
        }
        return model;
    }

    public static IModel customData(IModel model, ImmutableMap<String, String> customData)
    {
        if(model instanceof IModelCustomData)
        {
            model = ((IModelCustomData)model).process(customData);
        }
        return model;
    }

    public static IModel smoothLighting(IModel model, boolean smooth)
    {
        if(model instanceof IModelSimpleProperties)
        {
            model = ((IModelSimpleProperties)model).smoothLighting(smooth);
        }
        return model;
    }

    public static IModel gui3d(IModel model, boolean gui3d)
    {
        if(model instanceof IModelSimpleProperties)
        {
            model = ((IModelSimpleProperties)model).gui3d(gui3d);
        }
        return model;
    }

    public static IModel uvlock(IModel model, boolean uvlock)
    {
        if(model instanceof IModelUVLock)
        {
            model = ((IModelUVLock)model).uvlock(uvlock);
        }
        return model;
    }
}
