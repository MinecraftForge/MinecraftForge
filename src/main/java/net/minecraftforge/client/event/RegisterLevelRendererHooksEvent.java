package net.minecraftforge.client.event;

import java.util.function.Consumer;

import com.google.common.collect.Multimap;

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
    private final Multimap<LevelRendererHooks.Phase, Consumer<LevelRendererHooks.RenderContext>> hooksMap;

    public RegisterLevelRendererHooksEvent(Multimap<LevelRendererHooks.Phase, Consumer<LevelRendererHooks.RenderContext>> hooksMap)
    {
        this.hooksMap = hooksMap;
    }

    public void register(LevelRendererHooks.Phase phase, Consumer<LevelRendererHooks.RenderContext> hook)
    {
        this.hooksMap.put(phase, hook);
    }
}
