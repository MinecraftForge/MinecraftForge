/*
 * Minecraft Forge
 * Copyright (c) 2020.
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

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class BiomeExtensionType extends ForgeRegistryEntry<BiomeExtensionType>
{
    public static final Codec<IBiomeExtension> CODEC = ResourceLocation.field_240908_a_.<IBiomeExtension>dispatchMap(ex -> ex.getType().getRegistryName(), id -> ForgeRegistries.BIOME_EXTENSION_TYPES.getValue(id).getCodec()).codec();

    private final Codec<? extends IBiomeExtension> codec;

    public BiomeExtensionType(Codec<? extends IBiomeExtension> codec)
    {
        this.codec = codec;
    }

    public Codec<? extends IBiomeExtension> getCodec()
    {
        return codec;
    }
}
