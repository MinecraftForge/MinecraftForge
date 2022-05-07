package net.minecraftforge.client.event;

import java.util.function.Consumer;

import net.minecraftforge.client.LevelRendererHooks;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * Registers hooks to the level renderer after it has been created.
 * 
 * @see LevelRendererHooks
 */
public class RegisterLevelRendererHooksEvent extends Event implements IModBusEvent
{
    public void register(LevelRendererHooks.Phase phase, Consumer<LevelRendererHooks.RenderContext> hook)
    {
        LevelRendererHooks.register(phase, hook);
    }
}
