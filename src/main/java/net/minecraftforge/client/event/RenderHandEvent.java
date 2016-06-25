package net.minecraftforge.client.event;

import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.RenderGlobal;

@Cancelable
public class RenderHandEvent extends Event
{
    private final RenderGlobal context;
    private final float partialTicks;
    private final int renderPass;
    private final EnumHand hand;

    public RenderHandEvent(RenderGlobal context, float partialTicks, int renderPass, EnumHand hand)
    {
        this.context = context;
        this.partialTicks = partialTicks;
        this.renderPass = renderPass;
        this.hand = hand;
    }

    @Deprecated
    public RenderHandEvent(RenderGlobal context, float partialTicks, int renderPass)
    {
        this(context, partialTicks, renderPass, EnumHand.MAIN_HAND);
    }

    public RenderGlobal getContext()
    {
        return context;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public int getRenderPass()
    {
        return renderPass;
    }

    public EnumHand getHand()
    {
        return hand;
    }
}
