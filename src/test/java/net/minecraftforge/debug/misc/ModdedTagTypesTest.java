package net.minecraftforge.debug.misc;

import net.minecraft.potion.Effect;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.ForgeTagHandler;

@Mod(ModdedTagTypesTest.MODID)
public class ModdedTagTypesTest
{
    public static final String MODID = "modded_tag_types";

    public ModdedTagTypesTest() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        EffectTags.init();
    }

    static class EffectTags
    {
        //This needs to be initialised after registry events.
        private static final TagRegistry<Effect> effectTagRegistry = ForgeTagHandler.createTagType(ForgeRegistries.POTIONS);
        private static final ITag.INamedTag<Effect> testTag = makeWrapperTag("test_tag");

        private static ITag.INamedTag<Effect> makeWrapperTag(String path) {
            return effectTagRegistry.create(new ResourceLocation(MODID, path));
        }

        private static void init() {};
    }
}
