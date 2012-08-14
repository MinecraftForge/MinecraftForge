package net.minecraftforge.client.event.sound;

import net.minecraft.src.SoundManager;

public class SoundSetListenerEvent extends SoundEvent
{
    public final SoundManager manager;
    public final float elapsed;
    public final float posX;
    public final float posY;
    public final float posZ;
    public final float lookX;
    public final float lookY;
    public final float lookZ;
    public SoundSetListenerEvent(SoundManager manager, float elapsed, float posX, float posY, float posZ, float lookX, float lookY, float lookZ)
    {
        this.manager = manager;
        this.elapsed = elapsed;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
    }
}
