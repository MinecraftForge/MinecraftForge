package net.minecraftforge.client.event;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired on {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * after the {@link net.minecraft.client.model.ModelBiped#setRotationAngles(float, float, float, float, float, float, net.minecraft.entity.Entity)}  .
 * Allows to modify rotation of the model's parts before it gets rendered
 */
public class ModelPostRotationEvent extends Event{

	private ModelBiped mainModel;
	private Entity entity;
	private float limbSwing;
	private float limbSwingAmount;
	private float ageInTicks;
	private float netHeadYeaw;
	private float headPitch;
	private float scale;
	
	public ModelPostRotationEvent(ModelBiped mainModel, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.mainModel = mainModel;
		this.entity = entity;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.ageInTicks = ageInTicks;
		this.netHeadYeaw = netHeadYaw;
		this.headPitch = headPitch;
		this.scale = scale;
	}
	
	public ModelBiped getMainModel() {
		return mainModel;
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
