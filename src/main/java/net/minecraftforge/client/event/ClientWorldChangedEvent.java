package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * <p>
 * ClientWorldChangedEvent gets called whenever the main client world changes permanently for a session.
 * </p>
 * <p>
 * The oldWorld field will contain the world we were previously in, or null if we weren't in a world before.
 * The newWorld field will contain the world we're changing to, or null if we're quitting the world.
 * The loadingMessage field contains the loading message and can be modified to display a custom message
 * for clients when they're changing worlds. (Like how going to the nether used to work before)
 * </p>
 * <p>
 * This event gets fired from {@link net.minecraft.client.Minecraft#loadWorld(WorldClient, String)}.
 * </p>
 * <p>
 * <b>MODS THAT CHANGE THE WORLD:</b> If you change the world permanently for a session,
 * be sure to call this event to let other mods know about the world change!
 * Failing to do so can cause serious issues with mods relying on this event.
 * </p>
 */
public class ClientWorldChangedEvent extends Event
{
    public final WorldClient oldWorld;
    public final WorldClient newWorld;
    public String loadingMessage;

    public ClientWorldChangedEvent(WorldClient oldWorld, WorldClient newWorld, String loadingMessage)
    {
        this.oldWorld = oldWorld;
        this.newWorld = newWorld;
        this.loadingMessage = loadingMessage;
    }
}
