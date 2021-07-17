/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;

/**
 * PlaySoundAtEntityEvent is fired a sound is to be played at an Entity<br>
 * This event is fired whenever a sound is set to be played at an Entity such as in
 * {@link ClientPlayerEntity#playSound(SoundEvent, float, float)} and {@link World#playSound(PlayerEntity, double, double, double, SoundEvent, SoundCategory, float, float)}.<br>
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
@net.minecraftforge.eventbus.api.Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent
{
    private SoundEvent name;
    private SoundSource category;
    private final float volume;
    private final float pitch;
    private float newVolume;
    private float newPitch;

    public PlaySoundAtEntityEvent(Entity entity, SoundEvent name, SoundSource category, float volume, float pitch)
    {
        super(entity);
        this.name = name;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.newVolume = volume;
        this.newPitch = pitch;
    }

    public SoundEvent getSound() { return this.name; }
    public SoundSource getCategory() { return this.category; }
    public float getDefaultVolume() { return this.volume; }
    public float getDefaultPitch() { return this.pitch; }
    public float getVolume() { return this.newVolume; }
    public float getPitch() { return this.newPitch; }
    public void setSound(SoundEvent value) { this.name = value; }
    public void setCategory(SoundSource category) { this.category = category; }
    public void setVolume(float value) { this.newVolume = value; }
    public void setPitch(float value) { this.newPitch = value; }
}
