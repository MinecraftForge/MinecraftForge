package net.minecraftforge.debug.client.rendering;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ShaderEffectLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("shader_effect_load_event_test")
public class ShaderEffectLoadEventTest
{
    public ShaderEffectLoadEventTest()
    {
        if (FMLEnvironment.dist == Dist.CLIENT)
            MinecraftForge.EVENT_BUS.addListener(Client::onShaderEffectLoad);
    }

    static class Client
    {
        private static final ResourceLocation SHADER_SPIDER = new ResourceLocation("minecraft:shaders/post/spider.json");
        private static final ResourceLocation SHADER_GREEN = new ResourceLocation("minecraft:shaders/post/green.json");
        private static final ResourceLocation SHADER_INVERT = new ResourceLocation("minecraft:shaders/post/invert.json");
        public static void onShaderEffectLoad(ShaderEffectLoadEvent event)
        {
            ResourceLocation newEffect = event.getNewShaderEffect();

            // Replaces the "spider" effect with "green"
            if (SHADER_SPIDER.equals(newEffect))
                event.setNewShaderEffect(SHADER_GREEN);

            // Cancels the load of "enderman" effect
            else if (SHADER_INVERT.equals(newEffect))
                event.setNewShaderEffect(null);

        }
    }
}
