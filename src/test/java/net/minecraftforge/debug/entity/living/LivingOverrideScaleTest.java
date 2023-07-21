package net.minecraftforge.debug.entity.living;

import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(LivingOverrideScaleTest.MODID)
@Mod.EventBusSubscriber(modid=LivingOverrideScaleTest.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class LivingOverrideScaleTest {
    public static final String MODID = "living_override_scale_test";

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            MODID);


    public static final RegistryObject<EntityType<TestPig>> TEST_PIG = ENTITY_TYPES.register(
            "test_scale_pig", () ->
                    EntityType.Builder.of(TestPig::new, MobCategory.CREATURE)
                            .sized(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                            .build(new ResourceLocation(MODID, "test_scale_pig").toString())
    );

    public LivingOverrideScaleTest ()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITY_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void entityRegistry(EntityAttributeCreationEvent event)
    {
        event.put(TEST_PIG.get(), Pig.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = MODID, value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
    private static class LivingOverrideScaleClient
    {
        @SubscribeEvent
        public static void registerModels(EntityRenderersEvent.RegisterRenderers evt)
        {
            evt.registerEntityRenderer(TEST_PIG.get(), PigRenderer::new);
        }
    }

    public static class TestPig extends Pig {

        private final HumanoidArm someNullable;


        public TestPig(EntityType<? extends Pig> p_29462_, Level p_29463_) {
            super(p_29462_, p_29463_);
            someNullable = HumanoidArm.LEFT;
        }

        @Override
        public float getScale() {
            if (someNullable.ordinal() > 0){
                return 2.0f;
            } else {
                return 1.0f;
            }
        }
    }
}
