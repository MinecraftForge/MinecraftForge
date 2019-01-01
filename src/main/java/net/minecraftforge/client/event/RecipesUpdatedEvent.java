package net.minecraftforge.client.event;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when {@link RecipeManager} has all of its recipes updated.
 *
 * This happens on {@link Dist#CLIENT} when recipes are synced from the server to the client (just after a client has connected),
 * and on both {@link Dist#DEDICATED_SERVER} and {@link Dist#CLIENT} when datapacks are loaded or reloaded.
 */
public class RecipesUpdatedEvent extends Event
{
    public RecipesUpdatedEvent()
    {
    }
}
