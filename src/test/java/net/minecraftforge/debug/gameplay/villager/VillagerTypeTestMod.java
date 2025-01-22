/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.gameplay.villager;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

import java.util.Map;

@GameTestHolder("forge." + VillagerTypeTestMod.MOD_ID)
@Mod(VillagerTypeTestMod.MOD_ID)
public class VillagerTypeTestMod extends BaseTestMod {
    public static final String MOD_ID = "villager_type_test";

    private static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, MOD_ID);

    private static final RegistryObject<VillagerType> TEST_VILLAGER_TYPE = VILLAGER_TYPES.register("test_villager_type", () -> new VillagerType("test_villager_type"));

    public VillagerTypeTestMod(FMLJavaModLoadingContext context) {
        super(context);
        context.getModEventBus().addListener(this::onCommonSetup);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> VillagerType.registerBiomeType(Biomes.PLAINS, TEST_VILLAGER_TYPE.get()));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void biome_type(GameTestHelper helper) {
        RegistryAccess access = helper.getLevel().registryAccess();
        VillagerType type = access.registryOrThrow(Registries.VILLAGER_TYPE).get(TEST_VILLAGER_TYPE.getId());
        if (type == null)
            helper.fail("Failed to find test_villager_type");
        helper.assertValueEqual(type, TEST_VILLAGER_TYPE.get(), "Loaded entry does not contain expected value");

        Holder<Biome> biome = access.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
        helper.assertValueEqual(VillagerType.byBiome(biome), TEST_VILLAGER_TYPE.get(), "VillagerType.byBiome did not return the expected value");

        helper.succeed();
    }

    /** Test verifies NPE not thrown when looking up a villager type in the trade that doesn't contain that type.*/
    @GameTest(template = "forge:empty3x3x3")
    public static void emeralds_for_villager_type(GameTestHelper helper) {
        var trade = new VillagerTrades.EmeraldsForVillagerTypeItem(1, 12, 30, Map.of(TEST_VILLAGER_TYPE.get(), Items.DIRT));
        var rnd = helper.getLevel().getRandom();

        // Should be a successful trade for dirt for our test villager
        var test = new Villager(EntityType.VILLAGER, helper.getLevel(), TEST_VILLAGER_TYPE.get());
        var test_offer = trade.getOffer(test, rnd);
        helper.assertFalse(test_offer == null, "Failed to retreive trade value for test profession");
        helper.assertValueEqual(test_offer.getItemCostA().itemStack().getItem(), Items.DIRT, "Offer did not return the expected item");

        var plains = new Villager(EntityType.VILLAGER, helper.getLevel(), VillagerType.PLAINS);
        // This will NPE on unpatched code, we need to test that it returns null correctly
        var plains_offer = trade.getOffer(plains, rnd);
        helper.assertTrue(plains_offer == null, "Offer should not be available for a plains villager");

        helper.succeed();
    }
}
