/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

/**
 * Default implementation of IForgeRegistryEntry, this is necessary to reduce redundant code.
 * This also enables the registrie's ability to manage delegates. Which are automatically updated
 * if another entry overrides existing ones in the registry.
 */
@SuppressWarnings("unchecked")
public abstract class ForgeRegistryEntry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryEntry<V>
{
    private ResourceLocation registryName = null;
    private Holder.Reference<V> delegate = null;

    public final V setRegistryName(String name)
    {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = checkRegistryName(name);
        return (V)this;
    }

    //Helper functions
    public final V setRegistryName(ResourceLocation name){ return setRegistryName(name.toString()); }

    public final V setRegistryName(String modID, String name){ return setRegistryName(modID + ":" + name); }

    @Nullable
    public final ResourceLocation getRegistryName()
    {
        return this.getDelegate().map(ref -> ref.key().location()).orElse(this.registryName);
    }

    /**
     * This will assert that the registry name is valid and warn about potential registry overrides
     * It is important as it detects cases where modders unintentionally register objects with the "minecraft" namespace, leading to dangerous errors later.
     * @param name The registry name
     * @return A verified "correct" registry name
     */
    ResourceLocation checkRegistryName(String name)
    {
        return GameData.checkPrefix(name, true);
    }

    @NotNull
    @Override
    public Optional<Holder.Reference<V>> getDelegate()
    {
        return Optional.ofNullable(this.delegate);
    }

    @NotNull
    @Override
    public Holder.Reference<V> getDelegateOrThrow()
    {
        return getDelegate().orElseThrow(() -> new IllegalArgumentException(String.format(Locale.ENGLISH, "No delegate exists for key: %s, value: %s", registryName, this)));
    }

    @Override
    public void setDelegate(@NotNull Holder.Reference<V> delegate)
    {
        if (this.getDelegate().isPresent())
            throw new IllegalStateException("Attempted to set delegate after it was already set! New: " + delegate + " Old: " + this.getDelegateOrThrow());

        this.delegate = delegate;
    }

    /**
     * This class exists for registry entries which are dynamic (e.g. loaded via data packs), and also exist in a forge registry prior to that.
     * Due to this, the registry name will be set via the codec not during initial registration, and as a result, we want to not warn about possible overrides as the registry name will be set outside of mod context.
     */
    public abstract static class UncheckedRegistryEntry<V extends IForgeRegistryEntry<V>> extends ForgeRegistryEntry<V>
    {
        @Override
        ResourceLocation checkRegistryName(String name)
        {
            return new ResourceLocation(name);
        }
    }
}
