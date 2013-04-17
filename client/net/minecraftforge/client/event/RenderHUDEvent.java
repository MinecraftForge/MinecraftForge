package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.Event;

public class RenderHUDEvent extends Event {
	
	public final Minecraft mc;
	public final float partialTick;
	public final boolean guiOpen;
	public final int mouseX;
	public final int mouseY;
	
	public RenderHUDEvent(Minecraft mc, float partialTick, boolean guiOpen, int mouseX, int mouseY) {
		this.mc = mc;
		this.partialTick = partialTick;
		this.guiOpen = guiOpen;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
}
