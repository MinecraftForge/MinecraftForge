package net.minecraftforge.debug;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = NoPotionEffectRenderTest.modID, name = "No Potion Effect Render Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class NoPotionEffectRenderTest
{
    public static final String modID = "nopotioneffect";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        TestPotion INSTANCE = (TestPotion)new TestPotion(new ResourceLocation(modID, "test_potion"), false, 0xff00ff).setRegistryName(new ResourceLocation(modID, "test_potion"));
        ForgeRegistries.POTIONS.register(INSTANCE);
    }

    public static class TestPotion extends Potion
    {

        public TestPotion(ResourceLocation location, boolean badEffect, int potionColor)
        {
            super(badEffect, potionColor);
        }

        @Override
        public boolean shouldRender(PotionEffect effect)
        {
            return false;
        }
    }
}
