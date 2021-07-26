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

import java.util.Optional;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.biome.ParticleEffectAmbience;
import net.minecraft.world.biome.SoundAdditionsAmbience;

/**
 * Extension of the vanilla builder but also provides read access and a copy-from-existing-data helper.
 * Also, the base builder crashes if certain values aren't specified on build, so this enforces the setting of those 
 **/
public class BiomeAmbienceBuilder extends BiomeAmbience.Builder
{
    public static BiomeAmbienceBuilder copyFrom(BiomeAmbience baseEffects)
    {
        BiomeAmbienceBuilder builder = BiomeAmbienceBuilder.create(baseEffects.getFogColor(), baseEffects.getWaterColor(), baseEffects.getWaterFogColor(), baseEffects.getSkyColor());
        builder.grassColorModifier = baseEffects.getGrassColorModifier();
        baseEffects.getFoliageColorOverride().ifPresent(builder::foliageColorOverride);
        baseEffects.getGrassColorOverride().ifPresent(builder::grassColorOverride);
        baseEffects.getAmbientParticleSettings().ifPresent(builder::ambientParticle);
        baseEffects.getAmbientLoopSoundEvent().ifPresent(builder::ambientLoopSound);
        baseEffects.getAmbientMoodSettings().ifPresent(builder::ambientMoodSound);
        baseEffects.getAmbientAdditionsSettings().ifPresent(builder::ambientAdditionsSound);
        baseEffects.getBackgroundMusic().ifPresent(builder::backgroundMusic);
        return builder;
    }
    
    public static BiomeAmbienceBuilder create(int fogColor, int waterColor, int waterFogColor, int skyColor)
    {
        return new BiomeAmbienceBuilder(fogColor, waterColor, waterFogColor, skyColor);
    }
    
    protected BiomeAmbienceBuilder(int fogColor, int waterColor, int waterFogColor, int skyColor)
    {
        super();
        this.fogColor(fogColor);
        this.waterColor(waterColor);
        this.waterFogColor(waterFogColor);
        this.skyColor(skyColor);
    }
    
    public int getFogColor()
    {
        return this.fogColor.getAsInt();
    }
    
    public int waterColor()
    {
        return this.waterColor.getAsInt();
    }
    
    public int getWaterFogColor()
    {
        return this.waterFogColor.getAsInt();
    }
    
    public int getSkyColor()
    {
        return this.skyColor.getAsInt();
    }
    
    public BiomeAmbience.GrassColorModifier getGrassColorModifier()
    {
        return this.grassColorModifier;
    }
    
    public Optional<Integer> getFoliageColorOverride()
    {
        return this.foliageColorOverride;
    }
    
    public Optional<Integer> getGrassColorOverride()
    {
        return this.grassColorOverride;
    }
    
    public Optional<ParticleEffectAmbience> getAmbientParticle()
    {
        return this.ambientParticle;
    }
    
    public Optional<SoundEvent> getAmbientLoopSound()
    {
        return this.ambientLoopSoundEvent;
    }
    
    public Optional<MoodSoundAmbience> getAmbientMoodSound()
    {
        return this.ambientMoodSettings;
    }
    
    public Optional<SoundAdditionsAmbience> getAmbientAdditionsSound()
    {
        return this.ambientAdditionsSettings;
    }
    
    public Optional<BackgroundMusicSelector> getBackgroundMusic()
    {
        return this.backgroundMusic;
    }
}
