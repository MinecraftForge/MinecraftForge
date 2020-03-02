package net.minecraftforge.debug.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("stencil_enable_test")
public class StencilEnableTest {
    public StencilEnableTest() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        Minecraft.getInstance().getFramebuffer().enableStencil();
    }
}
