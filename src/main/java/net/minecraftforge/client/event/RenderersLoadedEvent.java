package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when loadRenderers method is called in RenderGlobal.java
 */
public class RenderersLoadedEvent extends Event {
    public final Minecraft mc;
    
    public RenderersLoadedEvent(Minecraft mc) {
	this.mc = mc;
    }
}
