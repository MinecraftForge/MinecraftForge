package net.minecraftforge.debug.client.rendering;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;


@Mod(RenderingPrePostTest.MODID)
@Mod.EventBusSubscriber(modid=RenderingPrePostTest.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class RenderingPrePostTest
{
    public static final String MODID = "rendering_pre_post_test";

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            RenderingPrePostTest.MODID);


    public static final RegistryObject<EntityType<Pig>> TEST_PIG = ENTITY_TYPES.register(
            "test_pig", () ->
                    EntityType.Builder.of(Pig::new, MobCategory.CREATURE)
                            .sized(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                            .build(new ResourceLocation(RenderingPrePostTest.MODID, "test_pig").toString())
    );

    public RenderingPrePostTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void entityRegistry(EntityAttributeCreationEvent event)
    {
        event.put(TEST_PIG.get(), Pig.createAttributes().build());
    }

    public static class SpecialPigRenderer extends PigRenderer {

        public final PigRenderer nestedRenderer;
        public SpecialPigRenderer(EntityRendererProvider.Context p_174340_) {
            super(p_174340_);
            nestedRenderer = new PigRenderer(p_174340_);
        }
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
    private static class RenderingPrePostTestClient
    {
        @SubscribeEvent
        public static void registerModels(EntityRenderersEvent.RegisterRenderers evt)
        {
            evt.registerEntityRenderer(TEST_PIG.get(), SpecialPigRenderer::new);
        }
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.FORGE)
    private static class RaiseHeightClient
    {

        public static final boolean DO_RAISE = false;
        @SubscribeEvent
        public static void entityPre(RenderLivingEvent.Pre<?, ?> evt)
        {
            if (DO_RAISE && evt.getEntity().getType().equals(TEST_PIG.get()))
            {
                evt.getPoseStack().pushPose();
                evt.getPoseStack().translate(0.0, 0.5, 0.0);
            }
        }

        @SubscribeEvent
        public static void entityPost(RenderLivingEvent.Post<?, ?> evt)
        {
            if (DO_RAISE && evt.getEntity().getType().equals(TEST_PIG.get()))
            {
                evt.getPoseStack().popPose();
            }
        }
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.FORGE)
    private static class DoublerClient
    {
        public static final boolean DO_DOUBLE = true;
        @SubscribeEvent
        public static void entityPre(RenderLivingEvent.Pre<?, ?> evt)
        {
            if (!evt.isCanceled() && DO_DOUBLE && evt.getEntity().getType().equals(TEST_PIG.get()))
            {
                evt.getPoseStack().pushPose();
                evt.getPoseStack().translate(0.0, 0.5, 0.0);
                if (evt.getRenderer() instanceof SpecialPigRenderer pig) {
                    pig.nestedRenderer.render((Pig)evt.getEntity(), 0.0f, evt.getPartialTick(), evt.getPoseStack(), evt.getMultiBufferSource(), evt.getPackedLight());
                }
                evt.getPoseStack().popPose();
            }
        }

    }
}