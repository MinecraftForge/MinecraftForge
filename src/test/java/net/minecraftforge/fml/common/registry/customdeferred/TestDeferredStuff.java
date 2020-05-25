package net.minecraftforge.fml.common.registry.customdeferred;


import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

public class TestDeferredStuff
{
   public static final DeferredRegister<TestRegistryObject> TESTS = new DeferredRegister<>(()->TestDeferredMod.testRegistry, TestDeferredMod.MOD_ID);
   public static final RegistryObject<TestRegistryObject> TEST_OBJECT = TESTS.register("test_object", ()->new TestRegistryObject("It worked"));
   public static final RegistryObject<TestRegistryObject> TEST_2 = TESTS.register("test_2", ()->new TestRegistryObject("It works perfectly."));

   public static void register()
   {
      TESTS.register(FMLJavaModLoadingContext.get().getModEventBus());
   }
}
