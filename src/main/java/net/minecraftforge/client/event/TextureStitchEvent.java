/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.texture.TextureMap;


public class TextureStitchEvent extends Event
{
    private final TextureMap map;

    public TextureStitchEvent(TextureMap map)
    {
        this.map = map;
    }

    public TextureMap getMap()
    {
        return map;
    }

    /**
     * Fired when the TextureMap is told to refresh it's stitched texture. 
     * Called after the Stitched list is cleared, but before any blocks or items
     * add themselves to the list.
     */
    public static class Pre extends TextureStitchEvent
    {
        public Pre(TextureMap map){ super(map); }
    }

    /**
     * This event is fired once the texture map has loaded all textures and 
     * stitched them together. All Icons should have there locations defined
     * by the time this is fired.
     */
    public static class Post extends TextureStitchEvent
    {
        public Post(TextureMap map){ super(map); }
    }
}
