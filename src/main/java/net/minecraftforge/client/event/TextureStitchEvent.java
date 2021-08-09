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

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;

/**
 * Fired before and after a texture atlas stitched together.
 * See the two subclasses to listen for before and after stitching.
 *
 * @see TextureStitchEvent.Pre
 * @see TextureStitchEvent.Post
 * @see TextureAtlas
 */
public class TextureStitchEvent extends Event implements IModBusEvent
{
    private final TextureAtlas atlas;

    public TextureStitchEvent(TextureAtlas atlas)
    {
        this.atlas = atlas;
    }

    /**
     * {@return the texture atlas}
     */
    public TextureAtlas getAtlas()
    {
        return atlas;
    }

    /**
     * <p>Fired <b>before</b> a texture atlas is stitched together. <br/>
     * This can be used to add custom sprites to be stitched into the atlas. </p>
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus()} mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ForgeHooksClient#onTextureStitchPre(TextureAtlas, Set)
     */
    public static class Pre extends TextureStitchEvent
    {
        private final Set<ResourceLocation> sprites;

        public Pre(TextureAtlas atlas, Set<ResourceLocation> sprites)
        {
            super(atlas);
            this.sprites = sprites;
        }

        /**
         * Adds a sprite to be stitched into the texture atlas.
         *
         * @param sprite the location of the sprite
         */
        public boolean addSprite(ResourceLocation sprite)
        {
            return this.sprites.add(sprite);
        }
    }

    /**
     * Fired <b>after</b> a texture atlas is stitched together.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus()} mod-specific event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ForgeHooksClient#onTextureStitchedPost(TextureAtlas)
     */
    public static class Post extends TextureStitchEvent
    {
        public Post(TextureAtlas atlas)
        {
            super(atlas);
        }
    }
}
