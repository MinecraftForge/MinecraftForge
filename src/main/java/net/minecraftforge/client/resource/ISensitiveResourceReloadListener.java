package net.minecraftforge.client.resource;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public interface ISensitiveResourceReloadListener extends IResourceManagerReloadListener
{
    @Override
    default void onResourceManagerReload(IResourceManager resourceManager)
    {
        // For compatibility, call the sensitive version from the insensitive function
        onResourceManagerReload(resourceManager, SensitiveReloadStateHandler.INSTANCE.get());
    }

    /**
     * An {@link net.minecraftforge.client.resource.IResourceType} sensitive version of onResourceManager reload.
     * When using this, the given {@link ReloadRequirements} should be checked to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager the resource manager being reloaded
     * @param requirements the requirements used to check whether relevant resources should be reloaded
     */
    void onResourceManagerReload(IResourceManager resourceManager, @Nonnull ReloadRequirements requirements);
}
