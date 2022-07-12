/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

import net.minecraft.data.CachedOutput;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

/**
 * <p>This test class shows an example of how to register unsynced and synced datapack registries,
 * and how to use a dataprovider to generate json files for them.
 * It also verifies that data and tags are loaded and synced correctly.
 * Data is loaded from the following folders in test resources:</p>
 * <ul>
 * <li><code>data/data_pack_registries_test/data_pack_registries_test/unsyncable/test.json</code></li>
 * <li><code>data/data_pack_registries_test/tags/data_pack_registries_test/unsyncable/test.json</code></li>
 * <li><code>data/data_pack_registries_test/data_pack_registries_test/syncable/test.json</code></li>
 * <li><code>data/data_pack_registries_test/tags/data_pack_registries_test/syncable/test.json</code></li>
 * </ul>
 */
@Mod(DataPackRegistriesTest.MODID)
public class DataPackRegistriesTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "data_pack_registries_test";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TEST_RL = new ResourceLocation(MODID, "test");

    private final RegistryObject<Unsyncable> datagenTestObject;

    public DataPackRegistriesTest()
    {
        if (!ENABLED)
            return;

        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // Deferred Registers can be created for datapack registries in static init or mod constructor.
        // (We'll do it in mod constructor, as when doing the ENABLED check it's less verbose than static init.)
        // As with static registries, any mod can make a Deferred Register for a given datapack registry,
        // but only one mod can register the internal registry with makeRegistry.
        final DeferredRegister<Unsyncable> unsyncables = DeferredRegister.create(Unsyncable.REGISTRY_KEY, MODID);
        final DeferredRegister<Syncable> syncables = DeferredRegister.create(Syncable.REGISTRY_KEY, MODID);

        // RegistryBuilder#dataPackRegistry marks the registry as a datapack registry rather than a static registry.
        unsyncables.makeRegistry(() -> new RegistryBuilder<Unsyncable>().disableSaving().dataPackRegistry(Unsyncable.DIRECT_CODEC));
        // The overload of #dataPackRegistry that takes a second codec marks the datapack registry as syncable.
        syncables.makeRegistry(() -> new RegistryBuilder<Syncable>().disableSaving().dataPackRegistry(Syncable.DIRECT_CODEC, Syncable.DIRECT_CODEC));

        // Datapack registry elements can be datagenerated, but they must be registered as builtin objects first.
        this.datagenTestObject = unsyncables.register("datagen_test", () -> new Unsyncable("Datagen Success"));

        unsyncables.register(modBus);
        syncables.register(modBus);

        modBus.addListener(this::onGatherData);
        forgeBus.addListener(this::onServerStarting);

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientEvents.subscribeClientEvents();
        }
    }

    private void onGatherData(final GatherDataEvent event)
    {
        // Example of how to datagen datapack registry objects.
        // Objects to be datagenerated must be registered (e.g. via DeferredRegister above).
        // This outputs to data/data_pack_registries_test/data_pack_registries_test/unsyncable/datagen_test.json
        final DataGenerator generator = event.getGenerator();
        final Path outputFolder = generator.getOutputFolder();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get());
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final ResourceLocation registryId = Unsyncable.REGISTRY_KEY.location();
        final ResourceLocation id = this.datagenTestObject.getId();
        final Unsyncable element = this.datagenTestObject.get();
        final String pathString = String.join("/", PackType.SERVER_DATA.getDirectory(), id.getNamespace(), registryId.getNamespace(), registryId.getPath(), id.getPath()+".json");
        final Path path = outputFolder.resolve(pathString);

        generator.addProvider(event.includeServer(), new DataProvider()
        {
            @Override
            public void run(final CachedOutput cache) throws IOException
            {
                Unsyncable.DIRECT_CODEC.encodeStart(ops, element)
                    .resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", path, msg)) // Log error on encode failure.
                    .ifPresent(json -> // Output to file on encode success.
                    {
                        try
                        {
                            DataProvider.saveStable(cache, json, path);
                        }
                        catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
                        {
                            LOGGER.error("Failed to save " + pathString, e);
                        }
                    });
            }

            @Override
            public String getName()
            {
                return String.format("%s provider for %s", registryId, MODID);
            }
        });
    }

    private void onServerStarting(final ServerStartingEvent event)
    {
        // Assert existence of json objects and tags.
        final RegistryAccess registries = event.getServer().registryAccess();
        final Registry<Unsyncable> registry = registries.registryOrThrow(Unsyncable.REGISTRY_KEY);
        final ResourceKey<Unsyncable> key = ResourceKey.create(Unsyncable.REGISTRY_KEY, TEST_RL);
        final Holder<Unsyncable> holder = registry.getHolderOrThrow(key);
        final Unsyncable testObject = registry.get(TEST_RL);
        if (!testObject.value().equals("success"))
            throw new IllegalStateException("Incorrect value loaded: " + testObject.value());
        final TagKey<Unsyncable> tag = TagKey.create(Unsyncable.REGISTRY_KEY, TEST_RL);
        if (!registry.getTag(tag).get().contains(holder))
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Tag %s does not contain %s", tag, TEST_RL));

        LOGGER.info("DataPackRegistriesTest server data loaded successfully!");
    }

    public static class ClientEvents
    {
        private static void subscribeClientEvents()
        {
            MinecraftForge.EVENT_BUS.addListener(ClientEvents::onClientTagsUpdated);
        }

        private static void onClientTagsUpdated(final TagsUpdatedEvent event)
        {
            // We want to check whether tags have been synced after the player logs in.
            // Tags are synced late in the login process and many relevant events fire before tags are synced.
            // TagsUpdatedEvent has the correct timing, but fires on both server and render thread,
            // so we need to make sure we're on the render thread.
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || EffectiveSide.get().isServer())
                return;

            // Assert existence of synced objects and tags. The TagsUpdatedEvent has its own RegistryAccess,
            // but we should check the player's connection's RegistryAccess as that's where the client's copy
            // lives during game runtime, and where mods should be querying on the client in most cases.
            RegistryAccess registries = player.connection.registryAccess();
            final Registry<Syncable> registry = registries.registryOrThrow(Syncable.REGISTRY_KEY);
            final ResourceKey<Syncable> key = ResourceKey.create(Syncable.REGISTRY_KEY, TEST_RL);
            final Holder<Syncable> holder = registry.getHolderOrThrow(key);
            final Syncable testObject = registry.get(TEST_RL);
            if (!testObject.value().equals("success"))
                throw new IllegalStateException("Incorrect value synced: " + testObject.value());
            final TagKey<Syncable> tag = TagKey.create(Syncable.REGISTRY_KEY, TEST_RL);
            if (!registry.getTag(tag).get().contains(holder))
                throw new IllegalStateException(String.format(Locale.ENGLISH, "Tag %s does not contain %s", tag, TEST_RL));

            LOGGER.info("DataPackRegistriesTest client data synced successfully!");
        }
    }

    public static class Unsyncable
    {
        public static final ResourceKey<Registry<Unsyncable>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "unsyncable"));
        public static final Codec<Unsyncable> DIRECT_CODEC = Codec.STRING.fieldOf("value").codec().xmap(Unsyncable::new, Unsyncable::value);

        private final String value;

        public Unsyncable(final String stringValue)
        {
            this.value = stringValue;
        }

        public String value()
        {
            return this.value;
        }
    }

    public static class Syncable
    {
        public static final ResourceKey<Registry<Syncable>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "syncable"));
        public static final Codec<Syncable> DIRECT_CODEC = Codec.STRING.fieldOf("value").codec().xmap(Syncable::new, Syncable::value);

        private final String value;

        public Syncable(final String value)
        {
            this.value = value;
        }

        public String value()
        {
            return this.value;
        }
    }
}