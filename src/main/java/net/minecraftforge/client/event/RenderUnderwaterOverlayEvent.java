package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired before the underwater overlay is rendered
 * This event is {@link Cancelable}
 * This event is fired from {@link net.minecraft.client.renderer.ItemRenderer#renderWaterOverlayTexture(float)}
 */
@Cancelable
public class RenderUnderwaterOverlayEvent extends Event
{
}
