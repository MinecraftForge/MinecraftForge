package net.minecraftforge.event.entity;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;

@Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent
{ 
    public String name;
    public final float volume;
    public final float pitch;
    
    public PlaySoundAtEntityEvent(Entity entity, String name, float volume, float pitch)
    {
        super(entity);
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }
}
