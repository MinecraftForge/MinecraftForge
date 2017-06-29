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

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.common.eventhandler.Event;


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
     * Event fired when a texture is registered to a TextureMap.
     */
    public static class TextureRegistered extends TextureStitchEvent {

        /**
         * The name of the texture that has been registered to the map.
         */
        private final String textureName;

        /**
         * Constructor used to create a new TextureStitchEvent.TextureRegistered Event.
         * @param map The TextureMap to which a new Texture has been registered.
         * @param textureName The name of the texture which has been registered.
         */
        public TextureRegistered(final TextureMap map, final String textureName)
        {
            super(map);
            this.textureName = textureName;
        }

        /**
         * Method to get the name of the texture that has been registered.
         * @return The name of the texture that has been registered.
         */
        public String getTextureName()
        {
            return textureName;
        }
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
