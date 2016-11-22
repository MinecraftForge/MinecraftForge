package net.minecraftforge.debug;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;

@Mod( modid = NoPotionEffectRenderTest.modID, name = "No Potion Effect Render Test", version = "0.0.0", acceptableRemoteVersions = "*" )
public class NoPotionEffectRenderTest {
  public static final String modID = "nopotioneffect";

  @Mod.EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    TestPotion INSTANCE = new TestPotion(new ResourceLocation(modID, "test_potion"), false, 0xff00ff);
    GameData.getPotionRegistry().register(-1, new ResourceLocation(modID, "test_potion"), INSTANCE);
  }

  public static class TestPotion extends Potion {

    public TestPotion(ResourceLocation location, boolean badEffect, int potionColor) {
      super(badEffect, potionColor);
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
      return false;
    }
  }
}
