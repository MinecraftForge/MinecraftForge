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

@Deprecated
public class ModelProcessingHelper
{
    public static IModel retexture(IModel model, ImmutableMap<String, String> textures)
    {
        return model.retexture(textures);
    }

    public static IModel customData(IModel model, ImmutableMap<String, String> customData)
    {
        return model.process(customData);
    }

    public static IModel smoothLighting(IModel model, boolean smooth)
    {
        return model.smoothLighting(smooth);
    }

    public static IModel gui3d(IModel model, boolean gui3d)
    {
        return model.gui3d(gui3d);
    }

    public static IModel uvlock(IModel model, boolean uvlock)
    {
        return model.uvlock(uvlock);
    }
}
