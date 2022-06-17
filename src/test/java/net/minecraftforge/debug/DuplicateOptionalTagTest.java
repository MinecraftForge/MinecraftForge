/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tests that the values for defaulted optional tags defined in multiple places are combined.
 *
 * <p>The optional tag defined by this mod is deliberately not defined in a data pack, to cause it to 'default' and
 * trigger the behavior being tested.</p>
 *
 * @see <a href="https://github.com/MinecraftForge/MinecraftForge/issues/7570">MinecraftForge/MinecraftForge#7570</>
 */
@Mod(DuplicateOptionalTagTest.MODID)
public class DuplicateOptionalTagTest
{
    private static final Logger LOGGER = LogManager.getLogger();

    static final String MODID = "duplicate_optional_tag_test";
    private static final ResourceLocation TAG_NAME = new ResourceLocation(MODID, "test_optional_tag");

    private static final Set<Supplier<Block>> TAG_A_DEFAULTS = Stream.of(Blocks.BEDROCK)
            .map(ForgeRegistries.BLOCKS::getDelegateOrThrow)
            .collect(Collectors.toUnmodifiableSet());
    private static final Set<Supplier<Block>> TAG_B_DEFAULTS = Stream.of(Blocks.WHITE_WOOL)
            .map(ForgeRegistries.BLOCKS::getDelegateOrThrow)
            .collect(Collectors.toUnmodifiableSet());

    private static final TagKey<Block> TAG_A = ForgeRegistries.BLOCKS.tags().createOptionalTagKey(TAG_NAME, TAG_A_DEFAULTS);
    private static final TagKey<Block> TAG_B = ForgeRegistries.BLOCKS.tags().createOptionalTagKey(TAG_NAME, TAG_B_DEFAULTS);

    public DuplicateOptionalTagTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    private void onServerStarted(ServerStartedEvent event)
    {
        Set<Block> tagAValues = ForgeRegistries.BLOCKS.tags().getTag(TAG_A).stream().collect(Collectors.toUnmodifiableSet());
        Set<Block> tagBValues = ForgeRegistries.BLOCKS.tags().getTag(TAG_B).stream().collect(Collectors.toUnmodifiableSet());

        if (!tagAValues.equals(tagBValues))
        {
            LOGGER.error("Values of both optional tag instances are not the same: first instance: {}, second instance: {}", tagAValues, tagBValues);
            return;
        }

        final Set<Block> expected = Sets.union(TAG_A_DEFAULTS, TAG_B_DEFAULTS).stream()
                .map(Supplier::get)
                .collect(Collectors.toUnmodifiableSet());
        if (!tagAValues.equals(expected))
        {
            IllegalStateException e = new IllegalStateException("Optional tag values do not match!");
            LOGGER.error("Values of the optional tag do not match the expected union of their defaults: expected {}, got {}", expected, tagAValues, e);
            return;
        }

        LOGGER.info("Optional tag instances match each other and the expected union of their defaults");
    }
}
