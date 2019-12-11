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

package net.minecraftforge.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

/**
 * For use when loading a custom model for use in entity/tileentity renderers, or other situations in which the main atlas is not wanted.
 */
/*
public class DummyAtlasSprite extends TextureAtlasSprite
{
    public static final TextureAtlasSprite INSTANCE = new DummyAtlasSprite();
    public static final Function<ResourceLocation, TextureAtlasSprite> GETTER = t -> INSTANCE;

    private DummyAtlasSprite()
    {
        super(new ResourceLocation("forge", "dummy"), 1, 1);
        // Set the min/max coords to 0..1 range, to use the full texture.
        func_217789_a(0, 0, 1, 1);
    }
}
*/