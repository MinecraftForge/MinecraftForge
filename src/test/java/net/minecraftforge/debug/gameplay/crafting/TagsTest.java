/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.gameplay.crafting;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftsingularity.common.Tags;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

@Mod(TagsTest.MODID)
@GameTestNamespace("singularity")
public final class TagsTest extends BaseTestMod {
    public static final String MODID = "tags_test";

    public TagsTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
    }

    @GameTest
    public static void cobble_in_common(GameTestHelper helper) throws ReflectiveOperationException {
        var cobble = new ItemStack(Items.COBBLESTONE);
        var commonCobbleTag = Tags.Items.COBBLESTONES;
        boolean isCobble = cobble.is(commonCobbleTag);
        helper.assertTrue(isCobble, commonCobbleTag + " is missing " + Items.COBBLESTONE);
        helper.succeed();
    }
}
