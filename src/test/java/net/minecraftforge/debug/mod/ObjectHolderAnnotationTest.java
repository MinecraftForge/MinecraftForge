/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.mod;

import net.minecraft.block.Block;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

//@Mod(modid = ObjectHolderAnnotationTest.MODID, name = "ObjectHolderTests", version = "1.0", acceptableRemoteVersions = "*")
public class ObjectHolderAnnotationTest
{
    public static final String MODID = "objectholdertest";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //verifies @ObjectHolder with custom id
        assert VanillaObjectHolder.uiButtonClick != null;
        //verifies modded items work
        assert ForgeObjectHolder.forge_potion != null;
        //verifies minecraft:air is now resolvable
        assert VanillaObjectHolder.air != null;
        //verifies unexpected name should not have defaulted to AIR.
        assert VanillaObjectHolder.nonExistentBlock == null;
        //verifies custom registries
        assert CustomRegistryObjectHolder.custom_entry != null;
        //verifies interfaces are supported
        assert CustomRegistryObjectHolder.custom_entry_by_interface != null;

    }

    protected static class PotionForge extends Potion
    {
        protected PotionForge(ResourceLocation location, boolean badEffect, int potionColor)
        {
            super(badEffect, potionColor);
            setPotionName("potion." + location.getResourcePath());
            setRegistryName(location);
        }
    }

    //@Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void newRegistry(RegistryEvent.NewRegistry event)
        {
            new RegistryBuilder<ICustomRegistryEntry>()
                    .setType(ICustomRegistryEntry.class)
                    .setName(new ResourceLocation("object_holder_test_custom_registry"))
                    .setIDRange(0, 255)
                    .create();
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerPotions(RegistryEvent.Register<Potion> event)
        {
            event.getRegistry().register(
                new ObjectHolderAnnotationTest.PotionForge(new ResourceLocation(ObjectHolderAnnotationTest.MODID, "forge_potion"), false, 0xff00ff) // test automatic id distribution
            );
        }

        @SubscribeEvent
        public static void registerInterfaceRegistryForge(RegistryEvent.Register<ICustomRegistryEntry> event)
        {
            event.getRegistry().register(
                new CustomRegistryEntry().setRegistryName(new ResourceLocation(MODID, "custom_entry_by_interface"))
            );

            event.getRegistry().register(
                new CustomRegistryEntry().setRegistryName(new ResourceLocation(MODID, "custom_entry"))
            );
        }
    }
    interface ICustomRegistryEntry extends IForgeRegistryEntry<ICustomRegistryEntry>{}


    static class CustomRegistryEntry implements ICustomRegistryEntry
    {
        private ResourceLocation name;

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
        public Class<ICustomRegistryEntry> getRegistryType()
        {
            return ICustomRegistryEntry.class;
        }
    }

    @GameRegistry.ObjectHolder("minecraft")
    static class VanillaObjectHolder
    {
        //Tests importing vanilla objects that need the @ObjectHolder annotation to get a valid ResourceLocation
        @GameRegistry.ObjectHolder("ui.button.click")
        public static final SoundEvent uiButtonClick = null;
        public static final Block air = null;
        public static final Block nonExistentBlock = null;
    }

    @GameRegistry.ObjectHolder(ObjectHolderAnnotationTest.MODID)
    static class ForgeObjectHolder
    {
        //Tests using subclasses for injections
        public static final ObjectHolderAnnotationTest.PotionForge forge_potion = null;
    }

    @GameRegistry.ObjectHolder(ObjectHolderAnnotationTest.MODID)
    static class CustomRegistryObjectHolder
    {
        //Tests whether custom registries can be used
        public static final ICustomRegistryEntry custom_entry = null;

        //Tests whether interfaces can be used
        public static final ICustomRegistryEntry custom_entry_by_interface = null;
    }
}
*/
