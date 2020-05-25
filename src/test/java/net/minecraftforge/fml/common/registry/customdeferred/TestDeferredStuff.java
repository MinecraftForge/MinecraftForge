package net.minecraftforge.fml.common.registry.customdeferred;


import net.minecraftforge.DeferredCustomRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TestDeferredStuff
{
   public static final DeferredCustomRegistry<TestRegistryObject> TESTS = new DeferredCustomRegistry<>(()->TestDeferredMod.testRegistry, TestDeferredMod.MOD_ID);
   public static final DeferredCustomRegistry.LazyOptionalRegistryObject<TestRegistryObject> TEST_OBJECT = TESTS.lazyOptRegister("test_object", ()->new TestRegistryObject("It worked"));
   public static final DeferredCustomRegistry.LazyOptionalRegistryObject<TestRegistryObject> TEST_2 = TESTS.lazyOptRegister("test_2", ()->new TestRegistryObject("It works perfectly."));

   public static void register()
   {
      TESTS.register(FMLJavaModLoadingContext.get().getModEventBus());
   }
}
