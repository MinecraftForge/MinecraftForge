/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraftforge.common.Tags.Enchantments.*;

@ApiStatus.Internal
public final class ForgeEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public ForgeEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        tag(INCREASE_BLOCK_DROPS)
                .add(Enchantments.FORTUNE);
        tag(INCREASE_ENTITY_DROPS)
                .add(Enchantments.LOOTING);
        tag(WEAPON_DAMAGE_ENHANCEMENTS)
                .add(Enchantments.SHARPNESS)
                .add(Enchantments.SMITE)
                .add(Enchantments.BANE_OF_ARTHROPODS)
                .add(Enchantments.POWER)
                .add(Enchantments.IMPALING);
        tag(ENTITY_SPEED_ENHANCEMENTS)
                .add(Enchantments.SOUL_SPEED)
                .add(Enchantments.SWIFT_SNEAK)
                .add(Enchantments.DEPTH_STRIDER);
        tag(ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS)
                .add(Enchantments.FEATHER_FALLING)
                .add(Enchantments.FROST_WALKER);
        tag(ENTITY_DEFENSE_ENHANCEMENTS)
                .add(Enchantments.PROTECTION)
                .add(Enchantments.BLAST_PROTECTION)
                .add(Enchantments.PROJECTILE_PROTECTION)
                .add(Enchantments.FIRE_PROTECTION)
                .add(Enchantments.RESPIRATION)
                .add(Enchantments.FEATHER_FALLING);
    }

    @Override
    public String getName() {
        return "Forge Enchantment Tags";
    }
}
