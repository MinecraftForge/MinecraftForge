package net.minecraftforge.fml.common.registry.customdeferred;


import net.minecraftforge.registries.DeferredCustomRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

public class TestDeferredStuff
{
   public static final DeferredRegister<TestRegistryObject> TESTS = new DeferredRegister<>(()->TestDeferredMod.testRegistry, TestDeferredMod.MOD_ID);
   public static final DeferredRegister.LazyRegistryObject<TestRegistryObject> TEST_OBJECT = TESTS.lazyRegister("test_object", ()->new TestRegistryObject("It worked"));
   public static final DeferredRegister.LazyRegistryObject<TestRegistryObject> TEST_2 = TESTS.lazyRegister("test_2", ()->new TestRegistryObject("It works perfectly."));

   public static void register()
   {
      TESTS.register(FMLJavaModLoadingContext.get().getModEventBus());
   }
}
