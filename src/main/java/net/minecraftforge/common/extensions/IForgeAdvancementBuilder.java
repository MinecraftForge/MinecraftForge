/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public interface IForgeAdvancementBuilder
{

    private Advancement.Builder self()
    {
        return (Advancement.Builder) this;
    }

    /**
     * Saves this builder with the given id using the {@link ExistingFileHelper} to check if the parent is already known.
     *
     * @param saver a {@link Consumer} which saves any advancements provided
     * @param id the {@link ResourceLocation} id for the new advancement
     * @param fileHelper the {@link ExistingFileHelper} where all known advancements are registered
     * @return the built advancement
     * @throws IllegalStateException if the parent of the advancement is not known
     */
    default Advancement save(Consumer<Advancement> saver, ResourceLocation id, ExistingFileHelper fileHelper)
    {
        boolean canBuild = self().canBuild((advancementId) ->
        {
            if (fileHelper.exists(advancementId, PackType.SERVER_DATA, ".json", "advancements"))
            {
                return new Advancement(advancementId, null, null, AdvancementRewards.EMPTY, Maps.newHashMap(), null);
            }
            return null;
        });
        if (!canBuild)
        {
            throw new IllegalStateException("Tried to build Advancement without valid Parent!");
        }

        Advancement advancement = self().build(id);
        saver.accept(advancement);
        return advancement;
    }
}
