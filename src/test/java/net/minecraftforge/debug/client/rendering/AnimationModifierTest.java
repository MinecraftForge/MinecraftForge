package net.minecraftforge.debug.client.rendering;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.extensions.IForgeEntityModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("animation_modifier_test")
public class AnimationModifierTest
{
    public static final boolean ENABLED = true;

    public AnimationModifierTest()
    {
        if (ENABLED && FMLEnvironment.dist.isClient())
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerLayersEvent);
        }
    }

    public void registerLayersEvent(EntityRenderersEvent.AddLayers event)
    {
        LivingEntityRenderer[] renderers = {
            event.getSkin("default"),
            event.getSkin("slim")
        };

        for (LivingEntityRenderer renderer : renderers)
        {
            Model m = renderer.getModel();

            if (m instanceof IForgeEntityModel fModel)
            {
                fModel.addAnimationModifier((entity, modelPart) -> {
                    ModelPart leftArm = modelPart.getChild("left_arm");
                    leftArm.xRot = (float)Math.PI;
                });
            }
        }
    }
}