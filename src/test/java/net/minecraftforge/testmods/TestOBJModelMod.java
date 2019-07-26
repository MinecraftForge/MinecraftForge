package net.minecraftforge.testmods;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("testobjmodelmod")
public class TestOBJModelMod {
    private static final Logger LOGGER = LogManager.getLogger();

    public TestOBJModelMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        OBJLoader.INSTANCE.addDomain("testobjmodelmod");
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onModelBakeEvent(ModelBakeEvent event) {
            try {
                // Try to load an OBJ model (placed in src/main/resources/assets/examplemod/models/)
                IUnbakedModel model = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("testobjmodelmod:sample_model.obj"));

                if (model instanceof OBJModel) {
                    // If loading OBJ model succeeds, bake the model and replace stick's model with the baked model
                    IBakedModel bakedModel = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.ITEM);
                    event.getModelRegistry().put(new ModelResourceLocation("stick", "inventory"), bakedModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
