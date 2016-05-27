package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.renderer.RenderGlobal;

@Cancelable
public class DrawBlockHighlightEvent extends Event
{
    private final RenderGlobal context;
    private final EntityPlayer player;
    private final RayTraceResult target;
    private final int subID;
    private final float partialTicks;

    public DrawBlockHighlightEvent(RenderGlobal context, EntityPlayer player, RayTraceResult target, int subID, float partialTicks)
    {
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.partialTicks= partialTicks;
    }

    public RenderGlobal getContext() { return context; }
    public EntityPlayer getPlayer() { return player; }
    public RayTraceResult getTarget() { return target; }
    public int getSubID() { return subID; }
    public float getPartialTicks() { return partialTicks; }
}
