package net.minecraftforge.fml.common.registry.customdeferred;

import net.minecraftforge.registries.DeferredRegister;

public class AddonDeferredStuff
{
   public static void init() {}

   public static final DeferredRegister.LazyRegistryObject<TestRegistryObject> TEST_ADDON = TestDeferredStuff.TESTS.lazyRegister("test_addon", ()->new TestRegistryObject("This test comes from the addon."));
}
