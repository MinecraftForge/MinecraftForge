/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.datagen;

import net.minecraft.core.Registry;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ValidationPredicate;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.event.GatherValidationPredicatesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Objects;

@Mod(ResourceValidationTest.MODID)
public class ResourceValidationTest
{
    private static final boolean ENABLED = false;
    public static final String MODID = "resource_validation_test";

    private static final boolean ENABLE_MODELS = true;
    private static final boolean ENABLE_TAGS = true;
    private static final boolean ERROR_TAGS = false;

    public ResourceValidationTest()
    {
        if (!ENABLED) return;

        if (!ERROR_TAGS)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener((final GatherValidationPredicatesEvent event) -> {
                event.and(((ValidationPredicate) (validationType, requestedPath, packType) -> packType == PackType.SERVER_DATA
                        && Objects.equals(validationType, ValidationPredicate.TAGS_VALIDATION_TYPE)
                        && requestedPath.equals(new ResourceLocation(MODID, "tags/blocks/test_sub.json"))).not());
            });
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener((final GatherDataEvent event) -> {
            if (ENABLE_MODELS)
                event.getGenerator().addProvider(event.includeClient(), new ItemModelProvider(event.getGenerator(), MODID, event.getExistingFileHelper())
                {
                    @Override
                    protected void registerModels()
                    {
                        basicItem(new ResourceLocation(MODID, "test"));
                    }
                });

            if (ENABLE_TAGS)
                event.getGenerator().addProvider(event.includeServer(), new BlockTagsProvider(event.getGenerator(), MODID, event.getExistingFileHelper())
                {
                    @Override
                    protected void addTags()
                    {
                        tag(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(MODID, "test")))
                                .add(TagEntry.tag(new ResourceLocation(MODID, "test_sub")));
                    }
                });
        });
    }
}
