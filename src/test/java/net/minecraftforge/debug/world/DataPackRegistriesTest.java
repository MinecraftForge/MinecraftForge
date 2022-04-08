package net.minecraftforge.debug.world;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.AddBuiltinRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

@Mod(DataPackRegistriesTest.MODID)
public class DataPackRegistriesTest
{
   public static final String MODID = "data_pack_registries_test";

   private static final Logger LOGGER = LogUtils.getLogger();

   private static final DeferredRegister<TestA> TEST_A_REGISTER = DataPackRegistriesHooks.createDataPackRegistry(TestA.REGISTRY, MODID, TestA.class);
   private static final DeferredRegister<TestB> TEST_B_REGISTER = DataPackRegistriesHooks.createDataPackRegistry(TestB.REGISTRY, MODID, TestB.class);

   private static final RegistryObject<TestA> BUILTIN_A = TEST_A_REGISTER.register("builtin_a", DataPackRegistriesTest::createBuiltinA);
   private static final RegistryObject<TestA> BUILTIN_A_OVERRIDDEN = TEST_A_REGISTER.register("builtin_a_overridden", DataPackRegistriesTest::createBuiltinA);
   private static final RegistryObject<TestB> BUILTIN_B = TEST_B_REGISTER.register("builtin_b", DataPackRegistriesTest::createBuiltinB);

   public DataPackRegistriesTest()
   {
      MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);

      var modBus = FMLJavaModLoadingContext.get().getModEventBus();
      TEST_A_REGISTER.register(modBus);
      TEST_B_REGISTER.register(modBus);
      modBus.addListener(this::onAddBuiltinRegistries);
   }

   private void onAddBuiltinRegistries(AddBuiltinRegistryEvent event)
   {
      event.addRegistry(TestA.REGISTRY, TestA.DIRECT_CODEC);
      event.addRegistry(TestB.REGISTRY, TestB.DIRECT_CODEC);
   }

   private void onServerAboutToStart(ServerAboutToStartEvent event)
   {
      var registries = event.getServer().registryAccess();
      printTestRegistries(registries);
      Optional<HolderSet.Named<TestA>> testATag = registries.ownedRegistryOrThrow(TestA.REGISTRY)
            .getTag(TagKey.create(TestA.REGISTRY, new ResourceLocation(MODID, "test_a_tag")));
      testATag.ifPresent(tag ->
            {
               LOGGER.info("DataPackRegistries Test Tag: test_a_tag:");
               int i = 0;
               for (Holder<TestA> tagElement : tag)
               {
                  if (tagElement instanceof Holder.Reference<TestA> tagElementReference)
                     LOGGER.info("  [{}] {}", i++, tagElementReference.key());
                  else
                     LOGGER.info("  [{}] String: {}, Int: {}, Biome: {}", i++,
                           tagElement.value().getStringValue(), tagElement.value().getIntValue(), tagElement.value().getBiome());
               }
            }
      );
   }

   private static TestA createBuiltinA()
   {
      return new TestA(
            "This is the default string value",
            12345,
            Holder.Reference.createStandAlone(BuiltinRegistries.BIOME, Biomes.PLAINS)
      );
   }

   @Nullable
   private static TestB createBuiltinB()
   {
      Optional<Holder<TestA>> builtin_a = DataPackRegistriesHooks.getHolder(BUILTIN_A.getKey());
      Optional<Holder<TestA>> builtin_a_overridden = DataPackRegistriesHooks.getHolder(ResourceKey.create(TestA.REGISTRY, new ResourceLocation(MODID, "builtin_a_overridden")));
      if (builtin_a.isEmpty() || builtin_a_overridden.isEmpty())
      {
         return null;
      }
      return new TestB(builtin_a.get(), List.of(builtin_a.get(), builtin_a_overridden.get()));
   }

   private static void printTestRegistries(RegistryAccess registries)
   {
      var biomeRegistry = registries.registryOrThrow(Registry.BIOME_REGISTRY);
      var testARegistry = registries.ownedRegistryOrThrow(TestA.REGISTRY);
      var testBRegistry = registries.ownedRegistryOrThrow(TestB.REGISTRY);

      LOGGER.info("DataPackRegistriesTest TestA:");
      for (var entry : testARegistry.entrySet())
      {
         var registryName = entry.getKey().location();

         var value = entry.getValue();
         var name = value.getStringValue();
         int intValue = value.getIntValue();
         var biomeName = biomeRegistry.getKey(value.getBiome().value());

         LOGGER.info(" - Registry Name: {}, String: {}, Int: {}, Biome: {} (valid={})", registryName, name, intValue, biomeName, biomeName != null);
      }

      LOGGER.info("DataPackRegistriesTest TestB:");
      for (var entry : testBRegistry.entrySet())
      {
         var registryName = entry.getKey().location();
         var value = entry.getValue();

         var testAName = testARegistry.getKey(value.getTestA().value());
         LOGGER.info(" - Registry Name: {}, TestA: {} (valid={}), TestAList:", registryName, testAName, testAName != null);

         for (int i = 0; i < value.getTestAList().size(); i++)
         {
            var element = value.getTestAList().get(i);
            var elementName = testARegistry.getKey(element.value());
            LOGGER.info("   [{}] {} (valid={})", i, elementName, elementName != null);
         }
      }
   }

   public static class TestA extends ForgeRegistryEntry<TestA>
   {
      public static final ResourceKey<Registry<TestA>> REGISTRY = DataPackRegistriesHooks.createRegistryKey(new ResourceLocation(MODID, "worldgen/test_a"));

      public static final Codec<TestA> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("string_value").forGetter(TestA::getStringValue),
            Codec.INT.fieldOf("int_value").forGetter(TestA::getIntValue),
            Biome.CODEC.fieldOf("biome").forGetter(TestA::getBiome)
      ).apply(instance, TestA::new));

      public static final RegistryFileCodec<TestA> CODEC = RegistryFileCodec.create(REGISTRY, DIRECT_CODEC);
      public static final Codec<List<Holder<TestA>>> LIST_CODEC = Codec.list(CODEC);

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
      public static final ResourceKey<Registry<TestB>> REGISTRY = DataPackRegistriesHooks.createRegistryKey(new ResourceLocation(MODID, "worldgen/test_b"));

      public static final Codec<TestB> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TestA.CODEC.fieldOf("test_a").forGetter(TestB::getTestA),
            TestA.LIST_CODEC.fieldOf("test_a_list").forGetter(TestB::getTestAList)
      ).apply(instance, TestB::new));

      private final Holder<TestA> testA;
      private final List<Holder<TestA>> testAList;

      public TestB(Holder<TestA> refB, List<Holder<TestA>> refsB)
      {
         this.testA = refB;
         this.testAList = refsB;
      }

      public Holder<TestA> getTestA()
      {
         return testA;
      }

      public List<Holder<TestA>> getTestAList()
      {
         return testAList;
      }
   }
}