package net.minecraftforge.debug.client.model;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.item.Items;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("render_living_model_test")
public class RenderLivingModelTestMod {

    public RenderLivingModelTestMod() {
        MinecraftForge.EVENT_BUS.addListener(this::onModelRender);
    }

    public void onModelRender(RenderLivingEvent.RenderModel event) {
        if(event.getEntity() instanceof AbstractClientPlayerEntity && event.getRenderer().getEntityModel() instanceof BipedModel) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)event.getEntity();
            BipedModel model = (BipedModel)event.getRenderer().getEntityModel();
            if(player.getHeldItemMainhand().getItem() == Items.TORCH) {
                model.bipedRightArm.rotateAngleX = -2F;
            }
        }
    }
}
