package net.minecraftforge.event;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * OPEvent is fired whenever a player is OPed or DeOped
 * <p>
 * {@link OP} is fired when a player is OPed<br>
 * {@link DeOP} is fired when a player is DeOped<br>
 * 
 * {@link #profile} contains the profile that was OPed or DeOPed
 * 
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class OPEvent extends Event {

    public final GameProfile profile;

    public OPEvent(GameProfile profile)
    {
        super();
        this.profile = profile;
    }

    /**
     * Fired when a player is OPed
     * <p>
     * This event is {@link Cancelable}<br>
     * If the event is canceled, the player will not be OPed
     */
    @Cancelable
    public static class OP extends OPEvent
    {
        public OP(GameProfile profile)
        {
            super(profile);
        }
    }

    /**
     * Fired when a player is DeOPed
     * <p>
     * This event is {@link Cancelable}<br>
     * If the event is canceled, the player will not be DeOPed
     */
    @Cancelable
    public static class DeOP extends OPEvent
    {
        public DeOP(GameProfile profile)
        {
            super(profile);
        }
    }

}
