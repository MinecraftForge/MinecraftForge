/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for {@link DimensionSpecialEffects} instances.
 * <p>
 * Provides a lookup by dimension type.
 */
public final class DimensionSpecialEffectsManager
{
    private static ImmutableMap<ResourceLocation, DimensionSpecialEffects> EFFECTS;
    private static DimensionSpecialEffects DEFAULT_EFFECTS;

    /**
     * Finds the {@link DimensionSpecialEffects} for a given dimension type, or the default if none is registered.
     */
    public static DimensionSpecialEffects getForType(ResourceLocation type)
    {
        return EFFECTS.getOrDefault(type, DEFAULT_EFFECTS);
    }

    @ApiStatus.Internal
    public static void init()
    {
        var effects = new HashMap<ResourceLocation, DimensionSpecialEffects>();
        DEFAULT_EFFECTS = preRegisterVanillaEffects(effects);
        var event = new RegisterDimensionSpecialEffectsEvent(effects);
        ModLoader.get().postEventWrapContainerInModOrder(event);
        EFFECTS = ImmutableMap.copyOf(effects);
    }

    /**
     * Pre-registers vanilla dimension effects and returns the default fallback effects instance.
     * <p>
     * Borrowed from {@link DimensionSpecialEffects#EFFECTS}.
     */
    private static DimensionSpecialEffects preRegisterVanillaEffects(Map<ResourceLocation, DimensionSpecialEffects> effects)
    {
        var overworldEffects = new DimensionSpecialEffects.OverworldEffects();
        effects.put(BuiltinDimensionTypes.OVERWORLD_EFFECTS, overworldEffects);
        effects.put(BuiltinDimensionTypes.NETHER_EFFECTS, new DimensionSpecialEffects.NetherEffects());
        effects.put(BuiltinDimensionTypes.END_EFFECTS, new DimensionSpecialEffects.EndEffects());
        return overworldEffects;
    }

    private DimensionSpecialEffectsManager()
    {
    }
}
