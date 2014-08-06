package net.minecraftforge.client;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;

/**
 * A more customizable implementation of {@link net.minecraft.client.audio.ISound ISound}.
 */
public class ForgeSound implements ISound
{
    private ResourceLocation soundLoc;
    private boolean canRepeat;
    private int repeatDelay;
    private float volume, pitch, xPos, yPos, zPos;
    private AttenuationType type;

    /**
     * @param sound The name of the sound to play
     */
    public ForgeSound(String sound)
    {
        this(new ResourceLocation(sound));
    }

    /**
     * @param sound The {@link net.minecraft.util.ResourceLocation ResourceLocation} of the sound to play
     */
    public ForgeSound(ResourceLocation sound)
    {
        this.soundLoc = sound;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.canRepeat = false;
        this.repeatDelay = 0;
        this.xPos = 0.0F;
        this.yPos = 0.0F;
        this.zPos = 0.0F;
        this.type = AttenuationType.NONE;
    }

    /**
     * Set the volume of an existing ForgeSound instance
     * @param volume The new volume of this ForgeSound
     */
    public ForgeSound setVolume(float volume)
    {
        this.volume = volume;
        return this;
    }

    /**
     * Set the pitch of an existing ForgeSound instance
     * @param pitch The new pitch of this ForgeSound
     */
    public ForgeSound setPitch(float pitch)
    {
        this.pitch = pitch;
        return this;
    }

    /**
     * Set the volume and pitch of an existing ForgeSound instance
     * @param volume The new volume of this ForgeSound
     * @param pitch The new pitch of this ForgeSound
     */
    public ForgeSound setVolumeAndPitch(float volume, float pitch)
    {
        this.volume = volume;
        this.pitch = pitch;
        return this;
    }

    /**
     * Make this ForgeSound instance repeatable
     * @param delay The delay between each repeat
     */
    public ForgeSound makeRepeatable(int delay)
    {
        this.canRepeat = true;
        this.repeatDelay = delay;
        return this;
    }

    /**
     * Set the location of an existing ForgeSound instance.<br/>
     * <B>This will change the {@link net.minecraft.client.audio.ISound.AttenuationType AttenuationType} to <I>LINEAR</I></B>
     * @param x The X position of where the sound should play
     * @param y The Y position of where the sound should play
     * @param z The Z position of where the sound should play
     */
    public ForgeSound setLocation(float x, float y, float z)
    {
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        this.type = AttenuationType.LINEAR;
        return this;
    }

    /**
     * Removes the location of this ForgeSound instance.<br/>
     * <B>This will change the {@link net.minecraft.client.audio.ISound.AttenuationType AttenuationType} to <I>NONE</I></B>
     */
    public ForgeSound removePosition()
    {
        this.xPos = this.yPos = this.zPos = 0.0F;
        this.type = AttenuationType.NONE;
        return this;
    }

    /**
     * Set the {@link net.minecraft.client.audio.ISound.AttenuationType AttenuationType} of an existng ForgeSound instance.
     * @param type The new AttenuationType of this ForgeSound
     */
    public ForgeSound setAttenuationType(AttenuationType type)
    {
        this.type = type;
        return this;
    }

    @Override
    public ResourceLocation getPositionedSoundLocation()
    {
        return soundLoc;
    }

    @Override
    public boolean canRepeat()
    {
        return canRepeat;
    }

    @Override
    public int getRepeatDelay()
    {
        return repeatDelay;
    }

    @Override
    public float getVolume()
    {
        return volume;
    }

    @Override
    public float getPitch()
    {
        return pitch;
    }

    @Override
    public float getXPosF()
    {
        return xPos;
    }

    @Override
    public float getYPosF()
    {
        return yPos;
    }

    @Override
    public float getZPosF()
    {
        return zPos;
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return type;
    }
}
