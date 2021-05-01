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

/**
 * An enum of all {@link IResourceType}s used by the Vanilla game. These should be used if handling vanilla-related
 * resources.
 */
public enum VanillaResourceType implements IResourceType
{
    /**
     * Used when block and item models are reloaded and rebaked. This also includes the texture-stitching from that
     * phase.
     */
    MODELS,

    /**
     * Used when textures from the {@link net.minecraft.client.renderer.texture.TextureManager} are reloaded. Does not
     * effect block or item textures on the texture atlas.
     */
    TEXTURES,

    /**
     * Used when all game sounds are reloaded.
     */
    SOUNDS,

    /**
     * Used when the current language is reloaded.
     */
    LANGUAGES,

    /**
     * Used when all shaders are reloaded.
     */
    SHADERS,
}
