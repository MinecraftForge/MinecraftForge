package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * after the {@link net.minecraft.client.renderer.entity.model.BipedModel#setRotationAngles(LivingEntity, float, float, float, float, float, float)}.
 * Allows to modify rotation of the model's parts before it gets rendered
 * If Canceled the model will not be rendered.
 */
@net.minecraftforge.eventbus.api.Cancelable
public class RenderBipedModelEvent extends Event {

    private BipedModel model;
    private Entity entity;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYeaw;
    private float headPitch;
    private float scale;

    public RenderBipedModelEvent(BipedModel model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.model = model;
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYeaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scale = scale;
    }

    public BipedModel getMainModel() {
        return model;
    }
    public Entity getEntity() {
        return entity;
    }
    public float getLimbSwing() {
        return limbSwing;
    }
    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }
    public float getAgeInTicks() {
        return ageInTicks;
    }
    public float getNetHeadYeaw() {
        return netHeadYeaw;
    }
    public float getHeadPitch() {
        return headPitch;
    }
    public float getScale() {
        return scale;
    }
}
