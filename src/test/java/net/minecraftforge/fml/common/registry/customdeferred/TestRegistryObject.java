package net.minecraftforge.fml.common.registry.customdeferred;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class TestRegistryObject extends ForgeRegistryEntry<TestRegistryObject>
{
   private final String test;

   public TestRegistryObject(String test)
   {
      this.test = test;
   }

   public void test()
   {
      TestDeferredMod.LOGGER.info(test);
   }
}
