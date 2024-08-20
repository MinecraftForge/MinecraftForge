/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.registries;

import java.util.Set;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge.network_data_registry")
@Mod(NetworkDatapackRegistryTest.MODID)
public class NetworkDatapackRegistryTest extends BaseTestMod {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "network_data_registry";
    private static final ResourceKey<Registry<DataObject>> REGISTRY_KEY = ResourceKey.createRegistryKey(rl(MODID, "registry"));
    private static final ResourceKey<DataObject> TEST_ENTRY = ResourceKey.create(REGISTRY_KEY, rl(MODID, "test_entry"));
    private static final ResourceLocation TEST_VALUE = rl(MODID, "test_value");

    public NetworkDatapackRegistryTest(FMLConstructModEvent event) {
        super(event);
    }

    @SubscribeEvent
    public void onNewDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY_KEY, DataObject.DIRECT_CODEC, DataObject.DIRECT_CODEC);
    }

    @SubscribeEvent
    public void onDataGen(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(),
            new RegistrySetBuilder()
                .add(REGISTRY_KEY,
                    ctx -> {
                        ctx.register(TEST_ENTRY, new DataObject(TEST_VALUE));
                    }
                ),
            Set.of(MODID))
        );
    }

    @GameTest(template = "forge:empty3x3x3")
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
        var reg = level.registryAccess().registry(REGISTRY_KEY);
        helper.assertTrue(reg.isPresent(), "Failed to find " + REGISTRY_KEY.location());
        var entry = reg.get().get(TEST_ENTRY);
        if (entry == null)
            helper.fail("Failed to find " + TEST_ENTRY);
        helper.assertValueEqual(entry.value, TEST_VALUE, "Loaded entry does not contain expected value");
    }

    public static record DataObject(ResourceLocation value) {
        public static final Codec<DataObject> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("value").forGetter(DataObject::value)
            ).apply(instance, DataObject::new));
    }
}
