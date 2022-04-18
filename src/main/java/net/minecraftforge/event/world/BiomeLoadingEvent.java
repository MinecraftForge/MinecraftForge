/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;

/**
 * This event fires when a Biome is created from json or when a registered biome is re-created for worldgen.
 * It allows mods to edit a biome (like add a mob spawn) before it gets used for worldgen.
 *
 * In order to maintain the most compatibility possible with other mods' modifications to a biome,
 * the event should be assigned a {@link net.minecraftforge.eventbus.api.EventPriority} as follows:
 *
 * - Additions to any list/map contained in a biome : {@link EventPriority#HIGH}
 * - Removals to any list/map contained in a biome : {@link EventPriority#NORMAL}
 * - Any other modification : {@link EventPriority#LOW}
 *
 * Be aware that another mod could have done an operation beforehand, so an expected value out of a vanilla biome might not
 * always be the same, depending on other mods.
 */
public class BiomeLoadingEvent extends Event
{
    private final ResourceLocation name;
    private Biome.ClimateSettings climate;
    private Biome.BiomeCategory category;
    private BiomeSpecialEffects effects;
    private final BiomeGenerationSettingsBuilder gen;
    private final MobSpawnSettingsBuilder spawns;

    public BiomeLoadingEvent(@Nullable final ResourceLocation name, final Biome.ClimateSettings climate, final Biome.BiomeCategory category, final BiomeSpecialEffects effects, final BiomeGenerationSettingsBuilder gen, final MobSpawnSettingsBuilder spawns)
    {
        this.name = name;
        this.climate = climate;
        this.category = category;
        this.effects = effects;
        this.gen = gen;
        this.spawns = spawns;
    }

    /**
     * This will get the registry name of the biome.
     * It generally SHOULD NOT be null, but due to vanilla's biome handling and codec weirdness, there may be cases where it is.
     * Do check for this possibility!
     */
    @Nullable
    public ResourceLocation getName()
    {
        return name;
    }

    public Biome.ClimateSettings getClimate()
    {
        return climate;
    }

    public void setClimate(final Biome.ClimateSettings value)
    {
        this.climate = value;
    }

    public Biome.BiomeCategory getCategory()
    {
        return category;
    }

    public void setCategory(final Biome.BiomeCategory value)
    {
        this.category = value;
    }

    public BiomeSpecialEffects getEffects()
    {
        return effects;
    }

    public void setEffects(final BiomeSpecialEffects value)
    {
        this.effects = value;
    }

    public BiomeGenerationSettingsBuilder getGeneration()
    {
        return gen;
    }

    public MobSpawnSettingsBuilder getSpawns()
    {
        return spawns;
    }
}