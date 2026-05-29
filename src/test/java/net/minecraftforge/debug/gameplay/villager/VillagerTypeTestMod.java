/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.gameplay.villager;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.test.BaseTestMod;

@GameTestNamespace("singularity")
@Mod(VillagerTypeTestMod.MOD_ID)
public class VillagerTypeTestMod extends BaseTestMod {
    public static final String MOD_ID = "villager_type_test";

    private static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, MOD_ID);

    private static final RegistryObject<VillagerType> TEST_VILLAGER_TYPE = VILLAGER_TYPES.register("test_villager_type", () -> new VillagerType());

    public VillagerTypeTestMod(FMLJavaModLoadingContext context) {
        super(context, false, true);
        FMLCommonSetupEvent.getBus(this.modBus).addListener(this::onCommonSetup);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> VillagerType.registerBiomeType(Biomes.PLAINS, TEST_VILLAGER_TYPE.getKey()));
    }

    @GameTest
    public static void biome_type(GameTestHelper helper) {
        RegistryAccess access = helper.getLevel().registryAccess();
        VillagerType type = access.lookupOrThrow(Registries.VILLAGER_TYPE).getValue(TEST_VILLAGER_TYPE.getId());
        if (type == null)
            helper.fail("Failed to find test_villager_type");
        helper.assertValueEqual(type, TEST_VILLAGER_TYPE.get(), Component.literal("Loaded entry does not contain expected value"));

        Holder<Biome> biome = access.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
        helper.assertValueEqual(VillagerType.byBiome(biome), TEST_VILLAGER_TYPE.getKey(), Component.literal("VillagerType.byBiome did not return the expected value"));

        helper.succeed();
    }
}
