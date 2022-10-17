package net.minecraftforge.debug.datagen;

import net.minecraft.core.Registry;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ResourceValidationTest.MODID)
public class ResourceValidationTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "resource_validation_test";

    private static final boolean ENABLE_MODELS = true;
    private static final boolean ENABLE_TAGS = false;

    public ResourceValidationTest()
    {
        if (!ENABLED) return;
        FMLJavaModLoadingContext.get().getModEventBus().addListener((final GatherDataEvent event) -> {
            if (ENABLE_MODELS)
                event.getGenerator().addProvider(event.includeClient(), new ItemModelProvider(event.getGenerator(), MODID, event.getExistingFileHelper())
                {
                    @Override
                    protected void registerModels()
                    {
                        basicItem(new ResourceLocation(MODID, "test"));
                    }
                });

            if (ENABLE_TAGS)
                event.getGenerator().addProvider(event.includeServer(), new BlockTagsProvider(event.getGenerator(), MODID, event.getExistingFileHelper())
                {
                    @Override
                    protected void addTags()
                    {
                        tag(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(MODID, "test")))
                                .add(TagEntry.tag(new ResourceLocation(MODID, "test_sub")));
                    }
                });
        });
    }
}
