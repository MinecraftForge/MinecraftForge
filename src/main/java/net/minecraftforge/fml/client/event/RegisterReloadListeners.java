package net.minecraftforge.fml.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IReloadableResourceManager;

/**
 * Fired just before {@link IReloadableResourceManager#initialReload} so mods can register listeners.
 * Fired on the mod event bus.
 *
 * Called on {@link Dist#CLIENT} - the game client.
 */
public class RegisterReloadListeners extends Event
{
	private final IReloadableResourceManager resourceManager;

	public RegisterReloadListeners(IReloadableResourceManager resourceManager)
	{
		this.resourceManager = resourceManager;
	}

	public void addReloadListener(IFutureReloadListener listener)
	{
		resourceManager.addReloadListener(listener);
	}
}
