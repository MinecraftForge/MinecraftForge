package net.minecraftforge.client.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.LogicalSide;

/**
 * Fired when {@linkplain Minecraft#pause Minecraft.pause} value got updated by the {@linkplain Minecraft#runTick(boolean) Minecraft.runTick(...)}
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class PauseGameEvent extends Event
{
    private final boolean paused;

    public PauseGameEvent(boolean isPaused)
    {
        this.paused = isPaused;
    }

    /**
     * {@return game is paused}
     */
    public boolean isPaused()
    {
        return paused;
    }
}