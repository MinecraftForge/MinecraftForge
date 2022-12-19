/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An extension of the {@link AdvancementProvider} to provide a feature-complete
 * experience to generate modded advancements.
 */
public class ForgeAdvancementProvider extends AdvancementProvider
{
    /**
     * Constructs an advancement provider using the generators to write the
     * advancements to a file.
     *
     * @param output the target directory of the data generator
     * @param registries a future of a lookup for registries and their objects
     * @param existingFileHelper a helper used to find whether a file exists
     * @param subProviders the generators used to create the advancements
     */
    public ForgeAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper, List<AdvancementGenerator> subProviders)
    {
        super(output, registries, subProviders.stream().map(generator -> generator.toSubProvider(existingFileHelper)).toList());
    }

    /**
     * An interface used to generated modded advancements. This is parallel to
     * vanilla's {@link AdvancementSubProvider} with access to the {@link ExistingFileHelper}.
     *
     * @see AdvancementSubProvider
     */
    public interface AdvancementGenerator
    {
        /**
         * A method used to generate advancements for a mod. Advancements should be
         * built via {@link net.minecraftforge.common.extensions.IForgeAdvancementBuilder#save(Consumer, ResourceLocation, ExistingFileHelper)}.
         *
         * @param registries a lookup for registries and their objects
         * @param saver a consumer used to write advancements to a file
         * @param existingFileHelper a helper used to find whether a file exists
         */
        void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper);

        /**
         * Creates an {@link AdvancementSubProvider} from this generator.
         *
         * @param existingFileHelper a helper used to find whether a file exists
         * @return a sub provider wrapping this generator
         */
        default AdvancementSubProvider toSubProvider(ExistingFileHelper existingFileHelper)
        {
            return (registries, saver) -> this.generate(registries, saver, existingFileHelper);
        }
    }
}
