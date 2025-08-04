/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.criterion;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.util.List;

@Mod(CriterionTest.MOD_ID)
@GameTestNamespace("forge")
public final class CriterionTest extends BaseTestMod {
    public static final String MOD_ID = "criterion_test";
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES.key(), MOD_ID);
    public static final RegistryObject<BreakWithItemCriterion> CRITERION = TRIGGERS.register("criterion", BreakWithItemCriterion::new);
    public static final String TEST_CRITERION_ID = "break_glass_with_fish";
    public static final ResourceLocation TEST_ADVANCEMENT_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, TEST_CRITERION_ID);

    public CriterionTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        GatherDataEvent.getBus(modBus).addListener(this::gatherData);
        BlockEvent.BreakEvent.BUS.addListener(this::onBlockBreak);
    }

    @GameTest
    public static void test_custom_criterion(GameTestHelper helper) {
        var advancement = helper.getLevel().getServer().getAdvancements().get(TEST_ADVANCEMENT_ID);

        helper.assertTrue(advancement != null, () -> "No advancement is present");

        var center = new BlockPos(1, 1, 1);
        helper.setBlock(center, Blocks.GLASS);
        helper.assertBlock(center, block -> block == Blocks.GLASS, block -> Component.literal("Failed to set glass block, was " + block.getDescriptionId()));

        var player = helper.makeMockServerPlayer();
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertFalse(player.getAdvancements().revoke(advancement, TEST_CRITERION_ID), "Advancement was granted to player in invalid conditions (breaking glass with diamond sword)");

        helper.setBlock(center, Blocks.DIRT);
        helper.assertBlock(center, block -> block == Blocks.DIRT, block -> Component.literal("Failed to set dirt block, was " + block.getDescriptionId()));
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertFalse(player.getAdvancements().revoke(advancement, TEST_CRITERION_ID), "Advancement was granted to player in invalid conditions (breaking dirt with diamond sword)");

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.COD));

        helper.setBlock(center, Blocks.DIRT);
        helper.assertBlock(center, block -> block == Blocks.DIRT,  block -> Component.literal("Failed to set dirt block, was " + block.getDescriptionId()));
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertFalse(player.getAdvancements().revoke(advancement, TEST_CRITERION_ID), "Advancement was granted to player in invalid conditions (breaking dirt with cod)");

        helper.setBlock(center, Blocks.GLASS);
        helper.assertBlock(center, block -> block == Blocks.GLASS,  block -> Component.literal("Failed to set glass block, was " + block.getDescriptionId()));
        player.gameMode.destroyBlock(helper.absolutePos(center));

        helper.assertTrue(player.getAdvancements().revoke(advancement, TEST_CRITERION_ID), "Advancement was not granted to player in correct conditions (breaking glass with cod)");

        helper.succeed();
    }

    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            CRITERION.get().trigger(player, event.getPos());
        }
    }

    private void gatherData(GatherDataEvent event) {
        var tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, "fish"));

        event.getGenerator().addProvider(true, new VanillaItemTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), MOD_ID, event.getExistingFileHelper()) {
            @Override
            protected void addTags(HolderLookup.Provider lookup) {
                this.tag(tag)
                    .add(Items.COD)
                    .add(Items.SALMON)
                    .add(Items.TROPICAL_FISH)
                    .add(Items.PUFFERFISH)
                    .add(Items.COOKED_COD)
                    .add(Items.COOKED_SALMON)
                ;
            }
        });

        var STORY_ROOT = new AdvancementHolder(ResourceLocation.fromNamespaceAndPath("minecraft", "story/root"), null);

        event.getGenerator().addProvider(true,
            new ForgeAdvancementProvider(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider(),
                event.getExistingFileHelper(),
                List.of(((registries, saver, existingFileHelper) -> {
                    var blocks = registries.lookup(Registries.BLOCK).get();
                    var items = registries.lookup(Registries.ITEM).get();
                    saver.accept(new Advancement.Builder()
                        .display(Items.COD,
                            Component.literal("Fish vs Glass"),
                            Component.literal("Fish wins!"),
                            null,
                            AdvancementType.TASK,
                            true, true, false
                        )
                        .parent(STORY_ROOT)
                        .requirements(AdvancementRequirements.Strategy.AND)
                        .addCriterion(TEST_CRITERION_ID, CRITERION.get().instance(
                            BlockPredicate.Builder.block().of(blocks, Tags.Blocks.GLASS_BLOCKS).build(),
                            ItemPredicate.Builder.item().of(items, tag).build(),
                            true
                        ))
                        .build(TEST_ADVANCEMENT_ID));
                }))
            )
        );
    }
}
