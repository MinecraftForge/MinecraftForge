/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(RemoveTagDatagenTest.MODID)
public class RemoveTagDatagenTest
{
    public static final String MODID = "remove_tag_datagen_test";
    public static final TagKey<Block> TEST_TAG = BlockTags.create(new ResourceLocation("test_tag"));

    public RemoveTagDatagenTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(true, new BlockTagsProvider(generator, MODID, helper)
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void addTags()
            {
                this.tag(TEST_TAG)
                    .remove(ForgeRegistries.BLOCKS.getKey(Blocks.DIRT))
                    .remove(ForgeRegistries.BLOCKS.getKey(Blocks.OAK_DOOR), ForgeRegistries.BLOCKS.getKey(Blocks.DARK_OAK_DOOR))
                    .remove(Blocks.ANVIL)
                    .remove(Blocks.BASALT, Blocks.POLISHED_BASALT)
                    .remove(BlockTags.BEEHIVES)
                    .remove(BlockTags.BANNERS, BlockTags.BEDS);
            }
        });
    }
}
