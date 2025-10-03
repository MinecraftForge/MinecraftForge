/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.registries;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegisterData;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(NetworkDatapackRegistryTest.MODID)
public class NetworkDatapackRegistryTest extends BaseTestMod {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "network_data_registry";
    private static final ResourceKey<Registry<DataObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(rl("registry"));

    private static final ResourceLocation TEST_VALUE = rl("test_value_new");

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
        helper.assertTrue(reg.isPresent(), "Failed to find " + REGISTRY_KEY.location());
        var entry = reg.get().getValue(REGISTRY_ENTRY.getKey());
        if (entry == null)
            helper.fail("Failed to find " + REGISTRY_ENTRY.getKey());
        helper.assertValueEqual(entry.value, TEST_VALUE, Component.literal("Loaded entry does not contain expected value"));
    }

    public static record DataObject(ResourceLocation value) {
        public static final Codec<DataObject> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("value").forGetter(DataObject::value)
            ).apply(instance, DataObject::new));
    }
}
