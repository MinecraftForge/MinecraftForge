package net.minecraftforge.event.advancement;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired when a script file is about to be read and used to construct an Advancement$Builder. <br>
 * <br>
 * {@link #location} is the {@link ResourceLocation} for the advancement. <br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If canceled, the script file will not be read, and the Advancement$Builder will not be constructed. <br>
 * 
 * @author Blargerist
 *
 */
@Cancelable
public class AdvancementAboutToLoadEvent extends Event
{
    private final ResourceLocation location;

    public AdvancementAboutToLoadEvent(final ResourceLocation location)
    {
        this.location = location;
    }

    public ResourceLocation getLocation()
    {
        return this.location;
    }
}
