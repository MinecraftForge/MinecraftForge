/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(RemoveTagDatagenTest.MODID)
public class RemoveTagDatagenTest {

    public static final String MODID = "remove_tag_datagen_test";
    public static final TagKey<Block> TEST_TAG = BlockTags.create(new ResourceLocation("test_tag"));

    public RemoveTagDatagenTest() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        var blocks = new BlockTagsProvider(generator.getPackOutput(), event.getLookupProvider(), MODID, helper) {
            @SuppressWarnings("unchecked")
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                this.tag(TEST_TAG)
                    .remove(key(Blocks.DIRT))
                    .remove(key(Blocks.OAK_DOOR), key(Blocks.DARK_OAK_DOOR))
                    .remove(key(Blocks.ANVIL))
                    .remove(key(Blocks.BASALT), key(Blocks.POLISHED_ANDESITE))
                    .remove(BlockTags.BEEHIVES)
                    .remove(BlockTags.BANNERS, BlockTags.BEDS);
            }
        };

        generator.addProvider(event.includeServer(), blocks);

        generator.addProvider(event.includeServer(), new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), blocks.contentsGetter(), MODID, helper) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                // This is for testing if it is functional, remove spruce_planks from planks, which makes us unable to craft beds with them.
                this.tag(ItemTags.PLANKS).remove(key(Blocks.SPRUCE_PLANKS));
                // This is for testing deep values, removing a entry in a tag that is referenced by another tag
                // Remove MUSIC_DISC_CAT from the MUSIC_DISCS tag, which is added by MUSIC_DISCS reference to the CREEPER_DROP_MUSIC_DISCS tag
                // This will make MUSIC_DISK_CAT unable to be played in a jukebox.
                // It will still remove the record from the player because RecordItem just blindly deletes, but it won't play music.
                this.tag(ItemTags.MUSIC_DISCS).remove(key(Items.MUSIC_DISC_CAT));
            }
        });
    }

    private static ResourceLocation key(Block value) {
         return ForgeRegistries.BLOCKS.getKey(value);
    }

    private static ResourceLocation key(Item value) {
         return ForgeRegistries.ITEMS.getKey(value);
    }
}
