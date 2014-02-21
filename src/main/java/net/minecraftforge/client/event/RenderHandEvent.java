package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.RenderGlobal;

@Cancelable
public class RenderHandEvent extends Event
{
    public final RenderGlobal context;
    public final float partialTicks;
    public final int renderPass;
    public RenderHandEvent(RenderGlobal context, float partialTicks, int renderPass)
    {
        this.context = context;
        this.partialTicks = partialTicks;
        this.renderPass = renderPass;
    }
}
