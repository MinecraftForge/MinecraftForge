package net.minecraftforge.event.entity;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;

/**
 * PlaySoundAtEntityEvent is fired a sound is to be played at an Entity<br>
 * This event is fired whenever a sound is set to be played at an Entity such as in
 * EntityPlayerSP#playSound(String, float, float), World#playSoundAtEntity(Entity, String, float, float),
 * and World#playerSoundToNearExcept(EntityPlayer, String, float, float).<br>
 * <br>
 * {@link #name} contains the name of the sound to be played at the Entity.<br>
 * {@link #volume} contains the volume at which the sound is to be played originally.<br>
 * {@link #pitch} contains the pitch at which the sound is to be played originally.<br>
 * {@link #newVolume} contains the volume at which the sound is actually played.<br>
 * {@link #newPitch} contains the pitch at which the sound is actually played.<br>
 * Changing the {@link #name} field will cause the sound of this name to be played instead of the originally intended sound.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the sound is not played.<br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent
{ 
    public String name;
    public final float volume;
    public final float pitch;
    public float newVolume;
    public float newPitch;
    
    public PlaySoundAtEntityEvent(Entity entity, String name, float volume, float pitch)
    {
        super(entity);
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
        this.newVolume = volume;
        this.newPitch = pitch;
    }
}
