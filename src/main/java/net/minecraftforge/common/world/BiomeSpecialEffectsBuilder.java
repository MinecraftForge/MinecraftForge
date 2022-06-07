/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import java.util.Optional;

import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

/**
 * Extension of the vanilla builder but also provides read access and a copy-from-existing-data helper.
 * Also, the base builder crashes if certain values aren't specified on build, so this enforces the setting of those.
 */
public class BiomeSpecialEffectsBuilder extends BiomeSpecialEffects.Builder
{
    public static BiomeSpecialEffectsBuilder copyOf(BiomeSpecialEffects baseEffects)
    {
        BiomeSpecialEffectsBuilder builder = BiomeSpecialEffectsBuilder.create(baseEffects.getFogColor(), baseEffects.getWaterColor(), baseEffects.getWaterFogColor(), baseEffects.getSkyColor());
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
    
    public static BiomeSpecialEffectsBuilder create(int fogColor, int waterColor, int waterFogColor, int skyColor)
    {
        return new BiomeSpecialEffectsBuilder(fogColor, waterColor, waterFogColor, skyColor);
    }
    
    protected BiomeSpecialEffectsBuilder(int fogColor, int waterColor, int waterFogColor, int skyColor)
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
    
    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier()
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
    
    public Optional<AmbientParticleSettings> getAmbientParticle()
    {
        return this.ambientParticle;
    }
    
    public Optional<SoundEvent> getAmbientLoopSound()
    {
        return this.ambientLoopSoundEvent;
    }
    
    public Optional<AmbientMoodSettings> getAmbientMoodSound()
    {
        return this.ambientMoodSettings;
    }
    
    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSound()
    {
        return this.ambientAdditionsSettings;
    }
    
    public Optional<Music> getBackgroundMusic()
    {
        return this.backgroundMusic;
    }
}