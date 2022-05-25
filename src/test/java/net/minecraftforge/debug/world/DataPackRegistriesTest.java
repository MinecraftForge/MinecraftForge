/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

@Mod(DataPackRegistriesTest.MODID)
public class DataPackRegistriesTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "data_pack_registries_test";

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<TestA> TEST_A_REGISTER;
    private static final DeferredRegister<TestB> TEST_B_REGISTER;
    private static final DeferredRegister<TestC> TEST_C_REGISTER;

    private static final RegistryObject<TestA> BUILTIN_A;
    private static final RegistryObject<TestA> BUILTIN_A_OVERRIDDEN;
    private static final RegistryObject<TestB> BUILTIN_B;

    static
    {
        if (ENABLED)
        {
            // registry A tests overwriting objects via json, loading non-builtin objects from json,
            // references to vanilla datapack registries (biomes), and tags.
            TEST_A_REGISTER = DeferredRegister.create(TestA.REGISTRY, MODID);
            TEST_A_REGISTER.makeRegistry(TestA.class, () -> new RegistryBuilder<TestA>().disableSaving().dataPackRegistry(TestA.DIRECT_CODEC));
            // registry B tests references to other custom datapack registries
            TEST_B_REGISTER = DeferredRegister.create(TestB.REGISTRY, MODID);
            TEST_B_REGISTER.makeRegistry(TestB.class, () -> new RegistryBuilder<TestB>().disableSaving().dataPackRegistry(TestB.DIRECT_CODEC));
            // registry C tests synced registries
            TEST_C_REGISTER = DeferredRegister.create(TestC.REGISTRY, MODID);
            TEST_C_REGISTER.makeRegistry(TestC.class, () -> new RegistryBuilder<TestC>().disableSaving().dataPackRegistry(TestC.DIRECT_CODEC, TestC.DIRECT_CODEC));

            // object not overridden by json
            BUILTIN_A = TEST_A_REGISTER.register("builtin_a", DataPackRegistriesTest::createBuiltinA);
            
            // object overridden by json
            BUILTIN_A_OVERRIDDEN = TEST_A_REGISTER.register("builtin_a_overridden", DataPackRegistriesTest::createBuiltinA);
            
            // object not overridden by json
            BUILTIN_B = TEST_B_REGISTER.register("builtin_b", DataPackRegistriesTest::createBuiltinB);
        }
        else
        {
            TEST_A_REGISTER = null;
            TEST_B_REGISTER = null;
            TEST_C_REGISTER = null;
            BUILTIN_A = null;
            BUILTIN_A_OVERRIDDEN = null;
            BUILTIN_B = null;
        }
    }

    public DataPackRegistriesTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
            MinecraftForge.EVENT_BUS.addListener(this::onRightClickBlock);

            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
            TEST_A_REGISTER.register(modBus);
            TEST_B_REGISTER.register(modBus);
            TEST_C_REGISTER.register(modBus);
        }
    }

    private void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        RegistryAccess registries = event.getServer().registryAccess();
        printTestRegistries(registries);
        Optional<HolderSet.Named<TestA>> testATag = registries.ownedRegistryOrThrow(TestA.REGISTRY)
            .getTag(TagKey.create(TestA.REGISTRY, new ResourceLocation(MODID, "test_a_tag")));
        testATag.ifPresent(tag -> {
            LOGGER.info("DataPackRegistries Test Tag: test_a_tag:");
            int i = 0;
            for (Holder<TestA> tagElement : tag)
            {
                if (tagElement instanceof Holder.Reference<TestA> tagElementReference)
                    LOGGER.info("  [{}] {}", i++, tagElementReference.key());
                else
                    LOGGER.info("  [{}] String: {}, Int: {}, Biome: {}", i++, tagElement.value().getStringValue(), tagElement.value().getIntValue(), tagElement.value().getBiome());
            }
        });
    }

    private static TestA createBuiltinA()
    {
        return new TestA("This is the default string value", 12345, Holder.Reference.createStandAlone(BuiltinRegistries.BIOME, Biomes.PLAINS));
    }

    @Nullable
    private static TestB createBuiltinB()
    {
        Optional<Holder<TestA>> builtin_a = BUILTIN_A.getHolder();
        Optional<Holder<TestA>> builtin_a_overridden = BUILTIN_A_OVERRIDDEN.getHolder();
        if (builtin_a.isEmpty() || builtin_a_overridden.isEmpty())
        {
            return null;
        }
        return new TestB(builtin_a.get(), HolderSet.direct(builtin_a.get(), builtin_a_overridden.get()));
    }

    private static void printTestRegistries(RegistryAccess registries)
    {
        Registry<Biome> biomeRegistry = registries.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<TestA> testARegistry = registries.ownedRegistryOrThrow(TestA.REGISTRY);
        Registry<TestB> testBRegistry = registries.ownedRegistryOrThrow(TestB.REGISTRY);

        LOGGER.info("DataPackRegistriesTest TestA:");
        for (var entry : testARegistry.entrySet())
        {
            ResourceLocation registryName = entry.getKey().location();

            TestA value = entry.getValue();
            String name = value.getStringValue();
            int intValue = value.getIntValue();
            ResourceLocation biomeName = biomeRegistry.getKey(value.getBiome().value());

            LOGGER.info(" - Registry Name: {}, String: {}, Int: {}, Biome: {} (valid={})", registryName, name, intValue, biomeName, biomeName != null);
        }

        LOGGER.info("DataPackRegistriesTest TestB:");
        for (var entry : testBRegistry.entrySet())
        {
            ResourceLocation registryName = entry.getKey().location();
            TestB value = entry.getValue();

            ResourceLocation testAName = testARegistry.getKey(value.getTestA().value());
            LOGGER.info(" - Registry Name: {}, TestA: {} (valid={}), TestAList:", registryName, testAName, testAName != null);

            for (int i = 0; i < value.getTestAList().size(); i++)
            {
                Holder<TestA> element = value.getTestAList().get(i);
                ResourceLocation elementName = testARegistry.getKey(element.value());
                LOGGER.info("   [{}] {} (valid={})", i, elementName, elementName != null);
            }
        }
    }
    
    private void onRightClickBlock(RightClickBlock event)
    {
        if (event.getPlayer() instanceof ServerPlayer player)
        {
            printSyncedRegistry(player.server.registryAccess());
        }
    }
    
    private static void printSyncedRegistry(RegistryAccess registries)
    {
        LOGGER.info("DataPackRegistriesTest TestC:");
        registries.registry(TestC.REGISTRY).ifPresent(registry ->
        {
            for (var entry : registries.registryOrThrow(TestC.REGISTRY).entrySet())
            {
                ResourceLocation name = entry.getKey().location();
                TestC value = entry.getValue();
                LOGGER.info(" - Registry Name: {}, Value: {}", name, value.value());
            }
        });
    }
    
    @EventBusSubscriber(modid = DataPackRegistriesTest.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents
    {
        @SubscribeEvent
        public static void onRightClickBlock(RightClickBlock event)
        {
            if (ENABLED)
            {
                if (event.getPlayer() instanceof LocalPlayer player)
                {
                    RegistryAccess registries = player.connection.registryAccess();
                    DataPackRegistriesTest.printSyncedRegistry(registries);
                }
            }
        }
    }

    public static class TestA extends ForgeRegistryEntry<TestA>
    {
        public static final ResourceKey<Registry<TestA>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, MODID + "/worldgen/test_a"));

        public static final Codec<TestA> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("string_value").forGetter(TestA::getStringValue),
                Codec.INT.fieldOf("int_value").forGetter(TestA::getIntValue),
                Biome.CODEC.fieldOf("biome").forGetter(TestA::getBiome)
            ).apply(instance, TestA::new));

        public static final RegistryFileCodec<TestA> CODEC = RegistryFileCodec.create(REGISTRY, DIRECT_CODEC);
        public static final Codec<HolderSet<TestA>> LIST_CODEC = RegistryCodecs.homogeneousList(REGISTRY);

        private final String stringValue;
        private final int intValue;
        private final Holder<Biome> biome;

        public TestA(String stringValue, int intValue, Holder<Biome> biome)
        {
            this.stringValue = stringValue;
            this.intValue = intValue;
            this.biome = biome;
        }

        public String getStringValue()
        {
            return stringValue;
        }

        public int getIntValue()
        {
            return intValue;
        }

        public Holder<Biome> getBiome()
        {
            return biome;
        }
    }

    public static class TestB extends ForgeRegistryEntry<TestB>
    {
        public static final ResourceKey<Registry<TestB>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, MODID + "/worldgen/test_b"));

        public static final Codec<TestB> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TestA.CODEC.fieldOf("test_a").forGetter(TestB::getTestA),
                TestA.LIST_CODEC.fieldOf("test_a_list").forGetter(TestB::getTestAList)
            ).apply(instance, TestB::new));

        private final Holder<TestA> testA;
        private final HolderSet<TestA> testAList;

        public TestB(Holder<TestA> refB, HolderSet<TestA> refsB)
        {
            this.testA = refB;
            this.testAList = refsB;
        }

        public Holder<TestA> getTestA()
        {
            return testA;
        }

        public HolderSet<TestA> getTestAList()
        {
            return testAList;
        }
    }
    public static class TestC extends ForgeRegistryEntry<TestC>
    {
        public static final ResourceKey<Registry<TestC>> REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, MODID + "/worldgen/test_c"));
        
        public static final Codec<TestC> DIRECT_CODEC = Codec.INT.fieldOf("value").codec().xmap(TestC::new, TestC::value);
        
        private final int value;
        
        public TestC(int value)
        {
            this.value = value;
        }
        
        public int value()
        {
            return this.value;
        }
    }
}