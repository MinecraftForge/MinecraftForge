/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.crafting;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@Mod(TagsTest.MODID)
@GameTestHolder("forge.crafting.tags")
public class TagsTest extends BaseTestMod {
    public static final String MODID = "tags_test";

    public TagsTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void cobble_in_legacy(GameTestHelper helper) throws ReflectiveOperationException {
        var cobble = new ItemStack(Items.COBBLESTONE);
        var forgeCobbleTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "cobblestone"));
        boolean isCobble = cobble.is(forgeCobbleTag);
        helper.assertTrue(isCobble, forgeCobbleTag + " is missing " + Items.COBBLESTONE);
        helper.succeed();
    }
}
