package org.bukkit.craftbukkit.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Maps;

public class CraftPotionBrewer implements PotionBrewer {
    private static final Map<Integer, Collection<PotionEffect>> cache = Maps.newHashMap();

    public Collection<PotionEffect> getEffectsFromDamage(int damage) {
        if (cache.containsKey(damage))
            return cache.get(damage);

        List<?> mcEffects = net.minecraft.potion.PotionHelper.getPotionEffects(damage, false);
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (mcEffects == null)
            return effects;

        for (Object raw : mcEffects) {
            if (raw == null || !(raw instanceof net.minecraft.potion.PotionEffect))
                continue;
            net.minecraft.potion.PotionEffect mcEffect = (net.minecraft.potion.PotionEffect) raw;
            PotionEffect effect = new PotionEffect(PotionEffectType.getById(mcEffect.getPotionID()),
                    mcEffect.getDuration(), mcEffect.getAmplifier());
            // Minecraft PotionBrewer applies duration modifiers automatically.
            effects.add(effect);
        }

        cache.put(damage, effects);

        return effects;
    }

    public PotionEffect createEffect(PotionEffectType potion, int duration, int amplifier) {
        return new PotionEffect(potion, potion.isInstant() ? 1 : (int) (duration * potion.getDurationModifier()),
                amplifier);
    }
}
