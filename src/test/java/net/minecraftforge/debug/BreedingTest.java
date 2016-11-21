package net.minecraftforge.debug;

import net.minecraft.entity.passive.EntityCow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = BreedingTest.MODID, name = "BreedingTest", version = "1.0")
public class BreedingTest
{
  public static final String MODID = "breedingtest";

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onBabyBorn(BabyEntitySpawnEvent event) {
    event.setChild(new EntityCow(event.getParentA().worldObj));
  }
}
