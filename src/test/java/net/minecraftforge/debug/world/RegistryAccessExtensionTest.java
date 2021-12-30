/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.RegistryAccessExtension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

@Mod(RegistryAccessExtensionTest.MODID)
public class RegistryAccessExtensionTest
{
    public static final String MODID = "registry_access_extension_test";

    private static final Logger LOGGER = LogManager.getLogger(MODID);

    private static final DeferredRegister<TestA> TEST_A_REGISTER = RegistryAccessExtension.createRegister(TestA.class, TestA.REGISTRY);
    private static final DeferredRegister<TestB> TEST_B_REGISTER = RegistryAccessExtension.createRegister(TestB.class, TestB.REGISTRY);

    private static final Supplier<TestA> BUILTIN_A = TEST_A_REGISTER.register("builtin_a", RegistryAccessExtensionTest::createBuiltinA);
    private static final Supplier<TestA> BUILTIN_A_OVERRIDDEN = TEST_A_REGISTER.register("builtin_a_overridden", RegistryAccessExtensionTest::createBuiltinA);
    private static final Supplier<TestB> BUILTIN_B = TEST_B_REGISTER.register("builtin_b", RegistryAccessExtensionTest::createBuiltinB);

    public RegistryAccessExtensionTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        TEST_A_REGISTER.register(modBus);
        TEST_B_REGISTER.register(modBus);

        modBus.addGenericListener(RegistryAccessExtension.class, this::registerExtension);
    }

    private void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        var registries = event.getServer().registryAccess();
        printTestRegistries(registries);
    }

    private void registerExtension(RegistryEvent.Register<RegistryAccessExtension<?>> event)
    {
        event.getRegistry().register(new RegistryAccessExtension<>(TestA.REGISTRY, TestA.DIRECT_CODEC));
        event.getRegistry().register(new RegistryAccessExtension<>(TestB.REGISTRY, TestB.DIRECT_CODEC));
    }

    private static TestA createBuiltinA()
    {
        return new TestA(
                "This is the default string value",
                12345,
                () -> ForgeRegistries.BIOMES.getValue(Biomes.PLAINS.location())
        );
    }

    private static TestB createBuiltinB()
    {
        return new TestB(BUILTIN_A, List.of());
    }

    private static void printTestRegistries(RegistryAccess registries)
    {
        var biomeRegistry = registries.registryOrThrow(Registry.BIOME_REGISTRY);
        var testARegistry = registries.ownedRegistryOrThrow(TestA.REGISTRY);
        var testBRegistry = registries.ownedRegistryOrThrow(TestB.REGISTRY);

        LOGGER.info("RegistryAccessExtension TestA:");
        for (var entry : testARegistry.entrySet())
        {
            var registryName = entry.getKey().location();

            var value = entry.getValue();
            var name = value.getStringValue();
            int intValue = value.getIntValue();
            var biomeName = biomeRegistry.getKey(value.getBiome().get());

            LOGGER.info(" - Registry Name: {}, String: {}, Int: {}, Biome: {} (valid={})", registryName, name, intValue, biomeName, biomeName != null);
        }

        LOGGER.info("RegistryAccessExtension TestB:");
        for (var entry : testBRegistry.entrySet())
        {
            var registryName = entry.getKey().location();
            var value = entry.getValue();

            var testAName = testARegistry.getKey(value.getTestA().get());
            LOGGER.info(" - Registry Name: {}, TestA: {} (valid={}), TestAList:", registryName, testAName, testAName != null);

            for (int i = 0; i < value.getTestAList().size(); i++)
            {
                var element = value.getTestAList().get(i);
                var elementName = testARegistry.getKey(element.get());
                LOGGER.info("   [{}] {} (valid={})", i, elementName, elementName != null);
            }
        }
    }

    public static class TestA extends ForgeRegistryEntry<TestA>
    {
        public static final ResourceKey<Registry<TestA>> REGISTRY = RegistryAccessExtension.createRegistryKey(MODID, "worldgen/test_a");

        public static final Codec<TestA> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("string_value").forGetter(TestA::getStringValue),
                Codec.INT.fieldOf("int_value").forGetter(TestA::getIntValue),
                Biome.CODEC.fieldOf("biome").forGetter(TestA::getBiome)
        ).apply(instance, TestA::new));

        public static final Codec<Supplier<TestA>> CODEC = RegistryFileCodec.create(REGISTRY, DIRECT_CODEC);
        public static final Codec<List<Supplier<TestA>>> LIST_CODEC = RegistryFileCodec.homogeneousList(REGISTRY, DIRECT_CODEC);

        private final String stringValue;
        private final int intValue;
        private final Supplier<Biome> biome;

        public TestA(String stringValue, int intValue, Supplier<Biome> biome)
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

        public Supplier<Biome> getBiome()
        {
            return biome;
        }
    }

    public static class TestB extends ForgeRegistryEntry<TestB>
    {
        public static final ResourceKey<Registry<TestB>> REGISTRY = RegistryAccessExtension.createRegistryKey(MODID, "worldgen/test_b");

        public static final Codec<TestB> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TestA.CODEC.fieldOf("test_a").forGetter(TestB::getTestA),
                TestA.LIST_CODEC.fieldOf("test_a_list").forGetter(TestB::getTestAList)
        ).apply(instance, TestB::new));

        private final Supplier<TestA> testA;
        private final List<Supplier<TestA>> testAList;

        public TestB(Supplier<TestA> refB, List<Supplier<TestA>> refsB)
        {
            this.testA = refB;
            this.testAList = refsB;
        }

        public Supplier<TestA> getTestA()
        {
            return testA;
        }

        public List<Supplier<TestA>> getTestAList()
        {
            return testAList;
        }
    }
}
