package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Allows modification to the entity model before being rendered.
 */
public class ModelRotationEvent extends Event{

	private final RenderLivingBase<?> render;
	private final float limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch;
	private final Entity entity;
	public ModelRotationEvent(RenderLivingBase<?> render, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
			float scaleFactor, Entity entity) {
		this.render=render;
		this.limbSwing=limbSwing;
		this.limbSwingAmount=limbSwingAmount;
		this.ageInTicks=ageInTicks;
		this.netHeadYaw=netHeadYaw;
		this.headPitch=headPitch;
		this.entity=entity;
	}
	
	public RenderLivingBase<?> getRender()
	{
		return this.render;
	}
	
	public float getLimbSwing()
	{
		return this.limbSwing;
	}
	
	public float getLimbSwingAmount()
	{
		return this.limbSwingAmount;
	}
	
	public float getAgeInTicks()
	{
		return this.ageInTicks;
	}
	
	public float getNetHeadYaw()
	{
		return this.netHeadYaw;
	}
	
	public float getHeadPitch()
	{
		return this.headPitch;
	}
	
	public Entity getEntity()
	{
		return this.entity;
	}
}
