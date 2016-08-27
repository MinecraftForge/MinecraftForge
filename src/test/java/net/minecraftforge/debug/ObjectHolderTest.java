package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid= ObjectHolderTest.MODID)
public class ObjectHolderTest
{
    public static final String MODID = "ObjectHolderTest";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Potion forge = new PotionForge(new ResourceLocation(MODID, "forgePotion"), false, 0xff00ff); // test automatic id distribution
        GameRegistry.register(forge);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //verifies @ObjectHolder with custom id
        assert VanillaObjectHolder.uiButtonClick != null;
        //verifies modded items work
        assert ForgeObjectHolder.forgePotion != null;
        //verifies modders can't mess with minecraft:air
        assert VanillaObjectHolder.air == null;
    }

    protected class PotionForge extends Potion {
        protected PotionForge(ResourceLocation location, boolean badEffect, int potionColor) {
            super(badEffect, potionColor);
            setPotionName("potion." + location.getResourcePath());
            setRegistryName(location);
        }
    }
}

@GameRegistry.ObjectHolder("minecraft")
class VanillaObjectHolder
{
    //Tests importing vanilla objects that need the @ObjectHolder annotation to get a valid ResourceLocation
    @GameRegistry.ObjectHolder("ui.button.click")
    public static final SoundEvent uiButtonClick = null;
    public static final Block air = null;
}

@GameRegistry.ObjectHolder(ObjectHolderTest.MODID)
class ForgeObjectHolder
{
    //Tests using subclasses for injections
    public static final ObjectHolderTest.PotionForge forgePotion = null;
}