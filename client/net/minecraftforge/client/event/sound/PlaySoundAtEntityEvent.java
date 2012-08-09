package net.minecraftforge.client.event.sound;

import net.minecraft.src.Entity;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlaySoundAtEntityEvent extends SoundEvent
{
    public final Entity entity; 
    public String name;
    public final float volume;
    public final float pitch;
    
    public PlaySoundAtEntityEvent(Entity entity, String name, float volume, float pitch)
    {
        this.entity = entity;
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }
}
