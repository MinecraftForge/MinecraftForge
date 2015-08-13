package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.RenderGlobal;

public class RenderWorldLastEvent extends Event
{
    public final RenderGlobal context;
    public final float partialTicks;
    public RenderWorldLastEvent(RenderGlobal context, float partialTicks)
    {
        this.context = context;
        this.partialTicks = partialTicks;
    }
}
