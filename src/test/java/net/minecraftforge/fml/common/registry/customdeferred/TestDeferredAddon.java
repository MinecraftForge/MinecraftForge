package net.minecraftforge.fml.common.registry.customdeferred;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;

@Mod(TestDeferredAddon.MOD_ID)
public class TestDeferredAddon
{
   public static final String MOD_ID = "custom_deferred_addon_testing";

   //This works, but probably not a good idea to static init an other mods classes...
   // Also makes this object be the first registered.
   public static final DeferredRegister.LazyRegistryObject<TestRegistryObject> TEST_ADDON2 = TestDeferredStuff.TESTS.lazyRegister("test_addon2", ()->new TestRegistryObject("This test comes from the static init addon."));

   public TestDeferredAddon()
   {
      AddonDeferredStuff.init(); //static init.
   }
}
