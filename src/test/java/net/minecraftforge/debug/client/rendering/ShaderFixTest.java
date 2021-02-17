package net.minecraftforge.debug.client.rendering;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("shader_fix_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShaderFixTest
{
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event)
    {
        ClientRegistry.registerEntityShader(ClientPlayerEntity.class, new ResourceLocation("shaders/post/desaturate.json"));
    }
}