package net.minecraftforge.debug;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod( modid = NoPotionEffectRenderTest.modID, name = "No Potion Effect Render Test", version = "0.0.0" )
public class NoPotionEffectRenderTest {
  public static final String modID = "nopotioneffect";

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    TestPotion INSTANCE = new TestPotion(30, new ResourceLocation(modID, "test_potion"), false, 0xff00ff);
  }

  public static class TestPotion extends Potion {

    public TestPotion(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
      return false;
    }
  }
}
