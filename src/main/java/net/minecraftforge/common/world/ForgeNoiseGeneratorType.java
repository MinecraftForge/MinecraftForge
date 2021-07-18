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

package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.FallbackKeyDispatchCodec;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeNoiseGeneratorSettings;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Registrable wrapper around a supplier for a noise generator setting codec (the things that read from the noise_settings folder).
 * Can be registered to {@link ForgeRegistries#NOISE_SETTING_CODECS}.
 * 
 * Beware! Mods' settings classes *must* override {@link IForgeNoiseGeneratorSettings#getType()} to return their ForgeNoiseGeneratorType instance.
 */
public class ForgeNoiseGeneratorType<NOISETYPE extends DimensionSettings> extends ForgeRegistryEntry<ForgeNoiseGeneratorType<?>>
{
    public static final Codec<ForgeNoiseGeneratorType<?>> CODEC = ResourceLocation.CODEC
        .comapFlatMap(id ->{
                ForgeNoiseGeneratorType<?> type = ForgeRegistries.NOISE_SETTING_CODECS.getValue(id);
                return type != null ? DataResult.success(type) : DataResult.error(String.format("No noise generator type registered to id %s", id));
            },
            ForgeNoiseGeneratorType::getRegistryName);

    public static final Codec<DimensionSettings> DISPATCH_CODEC = FallbackKeyDispatchCodec.dispatch(CODEC,
            settings -> settings.getType(),
            ForgeNoiseGeneratorType::getCodec,
            () -> ForgeMod.VANILLA_NOISE_GENERATOR_TYPE.get().getCodec());
        
    private final Codec<NOISETYPE> codec;
    public ForgeNoiseGeneratorType(Codec<NOISETYPE> codec)
    {
        this.codec = codec;
    }
    public Codec<NOISETYPE> getCodec()
    {
        return this.codec;
    }
}