package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

@Mod(modid= ObjectHolderTest.MODID, name = "ObjectHolderTests", version = "1.0")
public class ObjectHolderTest
{
    public static final String MODID = "objectholdertest";
    public static IForgeRegistry registry;

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        //verifies @ObjectHolder with custom id
        assert VanillaObjectHolder.uiButtonClick != null;
        //verifies modded items work
        assert ForgeObjectHolder.forgePotion != null;
        //verifies modders can't mess with minecraft:air
        assert VanillaObjectHolder.air == null;
        //verified unexpected name should not have defaulted to AIR.
        assert VanillaObjectHolder.nonExistentBlock == null;
        //verifies interfaces are supported
        assert CustomRegistryObjectHolder.customEntryByInterface != null;
    }

    protected static class PotionForge extends Potion {
        protected PotionForge(ResourceLocation location, boolean badEffect, int potionColor) {
            super(badEffect, potionColor);
            setPotionName("potion." + location.getResourcePath());
            setRegistryName(location);
        }
    }

    @Mod.EventBusSubscriber
    public static class Registration {
        @SubscribeEvent
        public static void newRegistry(RegistryEvent.NewRegistry event) {
            new RegistryBuilder()
                    .setType(ICustomRegistryEntry.class)
                    .setName(new ResourceLocation("ObjectHolderTestCustomRegistry"))
                    .create();
        }

        @SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event) {
            event.getRegistry().register(
                    new ObjectHolderTest.PotionForge(new ResourceLocation(ObjectHolderTest.MODID, "forgePotion"), false, 0xff00ff) // test automatic id distribution
            );
        }

        @SubscribeEvent
        public static void registerInterfaceRegistryForge(RegistryEvent.Register<ICustomRegistryEntry> event) {
            event.getRegistry().register(
                    new CustomRegistryEntry(new ResourceLocation(ObjectHolderTest.MODID, "customEntryByInterface"))
            );
        }
    }
}

class CustomRegistryEntry implements ICustomRegistryEntry
{
    private ResourceLocation name;

    protected CustomRegistryEntry(ResourceLocation location) {
        setRegistryName(location);
    }

    @Override
    public ICustomRegistryEntry setRegistryName(ResourceLocation name)
    {
        this.name = name;
        return this;
    }

    @Override
    public ResourceLocation getRegistryName()
    {
        return name;
    }

    @Override
    public Class<? super ICustomRegistryEntry> getRegistryType()
    {
        return ICustomRegistryEntry.class;
    }
}

interface ICustomRegistryEntry extends IForgeRegistryEntry<ICustomRegistryEntry> {

}

@GameRegistry.ObjectHolder("minecraft")
class VanillaObjectHolder
{
    //Tests importing vanilla objects that need the @ObjectHolder annotation to get a valid ResourceLocation
    @GameRegistry.ObjectHolder("ui.button.click")
    public static final SoundEvent uiButtonClick = null;
    public static final Block air = null;
    public static final Block nonExistentBlock = null;
}

@GameRegistry.ObjectHolder(ObjectHolderTest.MODID)
class ForgeObjectHolder
{
    //Tests using subclasses for injections
    public static final ObjectHolderTest.PotionForge forgePotion = null;
}

@GameRegistry.ObjectHolder(ObjectHolderTest.MODID)
class CustomRegistryObjectHolder
{
    //Tests whether interfaces can be used
    public static final CustomRegistryEntry customEntryByInterface = null;
}