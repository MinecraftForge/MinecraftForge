package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Event;

public class OrientCameraEvent extends Event
{
    public final float partialTicks;
    public final EntityPlayer player;
    
    public OrientCameraEvent(Minecraft mc, float partialTicks)
    {
        this.player = mc.thePlayer;
        this.partialTicks = partialTicks;
    }
}
