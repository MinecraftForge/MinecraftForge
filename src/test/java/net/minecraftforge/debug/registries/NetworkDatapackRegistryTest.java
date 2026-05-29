/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.registries;

import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.loading.FMLLoader;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.registries.DataPackRegistryEvent;
import net.minecraftsingularity.registries.DeferredRegisterData;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.test.BaseTestMod;

@GameTestNamespace("singularity")
@Mod(NetworkDatapackRegistryTest.MODID)
public class NetworkDatapackRegistryTest extends BaseTestMod {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "network_data_registry";
    private static final ResourceKey<Registry<DataObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(rl("registry"));

    private static final Identifier TEST_VALUE = rl("test_value_new");

    private static final DeferredRegisterData<DataObject> REGISTRY = DeferredRegisterData.create(REGISTRY_KEY, smodid());
    private static final RegistryObject<DataObject> REGISTRY_ENTRY = REGISTRY.register("test_entry", () -> new DataObject(TEST_VALUE));

    public NetworkDatapackRegistryTest(FMLJavaModLoadingContext context) {
        super(context, false, true);
        DataPackRegistryEvent.NewRegistry.BUS.addListener(this::onNewDatapackRegistry);
    }

    public void onNewDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY_KEY, DataObject.DIRECT_CODEC, DataObject.DIRECT_CODEC);
    }

    @GameTest
    public static void client_has_registry(GameTestHelper helper) {
        if (FMLLoader.getDist().isDedicatedServer())
            LOGGER.info("client_has_registry test skipped as we're on dedicated server");
        else
            client_has_registry_client_code(helper);
        helper.succeed();
    }

    // Separate function to not resolve classes until it's called, as it references client only classes.
    private static void client_has_registry_client_code(GameTestHelper helper) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var reg = level.registryAccess().lookup(REGISTRY_KEY);
        helper.assertTrue(reg.isPresent(), "Failed to find " + REGISTRY_KEY.identifier());
        var entry = reg.get().getValue(REGISTRY_ENTRY.getKey());
        if (entry == null)
            helper.fail("Failed to find " + REGISTRY_ENTRY.getKey());
        helper.assertValueEqual(entry.value, TEST_VALUE, Component.literal("Loaded entry does not contain expected value"));
    }

    public static record DataObject(Identifier value) {
        public static final Codec<DataObject> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("value").singularitytter(DataObject::value)
            ).apply(instance, DataObject::new));
    }
}
