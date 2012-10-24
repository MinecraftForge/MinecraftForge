package net.minecraftforge.client.event;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModelBiped;
import net.minecraftforge.event.Event;

public class PlayerModelRotationEvent extends Event
{
	public EntityPlayer currentPlayer;
	public ModelBiped model;
	public float ticks;
	public float legSwing;
	public float legYaw;
	public float bodyRotation;
	public float headYaw;
	public float headPitch;
	
	public PlayerModelRotationEvent(EntityPlayer currentPlayer, ModelBiped model, float par2, float par3, float par4, float par5, float par6, float par7)
	{
		this.currentPlayer = currentPlayer;
		this.model = model;
		this.ticks = par2;
		this.legSwing = par3;
		this.legYaw = par4;
		this.bodyRotation = par5;
		this.headYaw = par6;
		this.headPitch = par7;
	}
}