package net.minecraftforge.client.event;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collection;
import java.util.Collections;

/**
 * Fired when {@link RecipeManager} has its recipes updated.
 *
 * This happens on {@link Dist#CLIENT} when recipes are synced from the server to the client (just after a client has connected),
 * and on both {@link Dist#DEDICATED_SERVER} and {@link Dist#CLIENT} when resource packs are loaded or reloaded.
 */
public class RecipesUpdatedEvent extends Event
{
    private final Collection<IRecipe> recipes;

    public RecipesUpdatedEvent(Collection<IRecipe> recipes)
    {
        this.recipes = Collections.unmodifiableCollection(recipes);
    }

    public Collection<IRecipe> getRecipes()
    {
        return recipes;
    }
}
