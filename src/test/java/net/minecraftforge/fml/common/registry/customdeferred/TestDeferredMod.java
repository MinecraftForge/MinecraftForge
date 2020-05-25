package net.minecraftforge.fml.common.registry.customdeferred;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TestDeferredMod.MOD_ID)
public class TestDeferredMod
{
   public static final Logger LOGGER = LogManager.getLogger();
   public static IForgeRegistry<TestRegistryObject> testRegistry = null;
   public static final String MOD_ID = "custom_deferred_testing";

   public TestDeferredMod()
   {
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::createRegistry);
      TestDeferredStuff.register();
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::test);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::test2);
   }

   private void createRegistry(RegistryEvent.NewRegistry event)
   {
      testRegistry = new RegistryBuilder<TestRegistryObject>()
              .setType(TestRegistryObject.class)
              .setName(new ResourceLocation(MOD_ID, "test_registry"))
              .create();
   }

   private void test(FMLCommonSetupEvent event)
   {
      TestDeferredStuff.TESTS.getEntries().forEach(test->test.get().test());
      TestDeferredStuff.TEST_2.get().test();
   }

   private void test2(FMLClientSetupEvent event)
   {
      TestDeferredStuff.TESTS.getEntries().forEach(test->test.get().test());
      TestDeferredStuff.TEST_OBJECT.get().test();
   }
}
