/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;

/**
 * EntityEmittedSoundEvent is fired when an entity requests that a sound is played in its corresponding {@link net.minecraft.world.World}.<br>
 * This event is fired in
 * {@link net.minecraftforge.common.extensions.IForgeWorld.playSoundFromEntity(Entity, double, double, double, SoundEvent, SoundCategory, float, float)}.<br>
 * <br>
 * {@link #name} contains the name of the sound to be played at the Entity.<br>
 * {@link #position} represents the position that the sound was emitted at in the world.<br>
 * {@link #volume} contains the volume at which the sound is to be played originally.<br>
 * {@link #pitch} contains the pitch at which the sound is to be played originally.<br>
 * {@link #newVolume} contains the volume at which the sound is actually played.<br>
 * {@link #newPitch} contains the pitch at which the sound is actually played.<br>
 * Changing the {@link #name} field will cause the sound of this name to be played instead of the originally intended sound.<br>
 * Similarly, changing the {@link #position} will move the effective position of the sound. Setting this to a null value will cause the sound to not be played.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the sound is not played.<br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@Cancelable
public class EntityEmittedSoundEvent extends EntityEvent
{
    private SoundEvent name;
    private SoundCategory category;
    private final float volume;
    private final float pitch;
    private float newVolume;
    private float newPitch;
    private Vector3d position;

    public EntityEmittedSoundEvent(Entity source, Vector3d position, SoundEvent name, SoundCategory category, float volume, float pitch)
    {
        super(source);
        this.name = name;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.position = position;
        this.newVolume = volume;
        this.newPitch = pitch;
    }

    public SoundEvent getSound() { return this.name; }
    public SoundCategory getCategory() { return this.category; }
    public float getDefaultVolume() { return this.volume; }
    public float getDefaultPitch() { return this.pitch; }
    public float getVolume() { return this.newVolume; }
    public float getPitch() { return this.newPitch; }
    public Vector3d getPosition() { return this.position; }
    public void setSound(SoundEvent value) { this.name = value; }
    public void setCategory(SoundCategory category) { this.category = category; }
    public void setVolume(float value) { this.newVolume = value; }
    public void setPitch(float value) { this.newPitch = value; }
    public void setPosition(Vector3d value) { this.position = value; }
}
