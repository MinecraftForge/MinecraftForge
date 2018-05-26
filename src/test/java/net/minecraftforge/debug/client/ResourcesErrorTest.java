package net.minecraftforge.debug.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "resources_error_test", name = "ResourceErrorTest", clientSideOnly = true)
@Mod.EventBusSubscriber
public class ResourcesErrorTest
{

    private static final boolean ENABLE = false;

    @SubscribeEvent
    public static void init(ModelRegistryEvent evt)
    {
        if (ENABLE)
            ModelLoader.setCustomModelResourceLocation(Items.DIAMOND, 1, new ModelResourceLocation("resources_error_test:missing#var0"));
    }

    @SubscribeEvent
    public static void init(TextureStitchEvent.Pre evt)
    {
        if (ENABLE)
            evt.getMap().registerSprite(new ResourceLocation("resources_error_test:missingno"));
    }
}
