package net.minecraftforge.client.resource;

import java.util.function.Predicate;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public interface ISelectiveResourceReloadListener extends IResourceManagerReloadListener
{
    @Override
    default void onResourceManagerReload(IResourceManager resourceManager)
    {
        // For compatibility, call the sensitive version from the insensitive function
        onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
    }

    /**
     * An {@link net.minecraftforge.client.resource.IResourceType} sensitive version of onResourceManager reload.
     * When using this, the given predicate should be called to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager the resource manager being reloaded
     * @param resourcePredicate predicate to test whether any given resource type should be reloaded
     */
    void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate);
}
