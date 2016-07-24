package net.minecraftforge.client.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * whenever a hand is rendered in first person.
 * Canceling the event causes the hand to not render.
 */
@Cancelable
public class RenderHandEventSpecific extends Event
{
    private final EnumHand hand;
    private final float partialTicks;
    private final float interpolatedPitch;
    private final float swingProgress;
    private final float equipProgress;
    private final ItemStack stack;

    public RenderHandEventSpecific(EnumHand hand, float partialTicks, float interpolatedPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        this.hand = hand;
        this.partialTicks = partialTicks;
        this.interpolatedPitch = interpolatedPitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.stack = stack;
    }

    public EnumHand getHand()
    {
        return hand;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    /**
     * @return The interpolated pitch of the player entity
     */
    public float getInterpolatedPitch()
    {
        return interpolatedPitch;
    }

    /**
     * @return The swing progress of the hand being rendered
     */
    public float getSwingProgress()
    {
        return swingProgress;
    }

    /**
     * @return The progress of the equip animation. 1.0 is fully equipped.
     */
    public float getEquipProgress()
    {
        return equipProgress;
    }

    /**
     * @return The ItemStack to be rendered, or null.
     */
    @Nullable
    public ItemStack getItemStack()
    {
        return stack;
    }
}
