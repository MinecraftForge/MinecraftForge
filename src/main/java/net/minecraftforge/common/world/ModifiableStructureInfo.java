/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.world;

import java.util.List;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds lazy-evaluable modified structure info.
 * Memoizers are not used because it's important to return null
 * without evaluating the structure info if it's accessed outside of a server context.
 */
public class ModifiableStructureInfo
{
    @NotNull
    private final StructureInfo originalStructureInfo;
    @Nullable
    private StructureInfo modifiedStructureInfo = null;

    /**
     * @param originalStructureInfo StructureInfo representing the original state of a structure when the structure was constructed.
     */
    public ModifiableStructureInfo(@NotNull final StructureInfo originalStructureInfo)
    {
        this.originalStructureInfo = originalStructureInfo;
    }
    
    /**
     * {@return The modified structure info if modified structure info has been generated, otherwise gets original structure info}
     */
    @NotNull
    public StructureInfo get()
    {
        return this.modifiedStructureInfo == null
            ? originalStructureInfo
            : modifiedStructureInfo;
    }
    
    /**
     * {@return The original structure info that the associated structure was created with}
     */
    @NotNull
    public StructureInfo getOriginalStructureInfo()
    {
        return this.originalStructureInfo;
    }
    
    /**
     * {@return Modified structure info; null if it hasn't been set yet}
     */
    @Nullable
    public StructureInfo getModifiedStructureInfo()
    {
        return this.modifiedStructureInfo;
    }
    
    /**
     * Internal forge method; the game will crash if mods invoke this.
     * Creates and caches the modified structure info.
     * @param structure named structure with original data.
     * @param structureModifiers structure modifiers to apply.
     * 
     * @throws IllegalStateException if invoked more than once.
     */
    @ApiStatus.Internal
    public void applyStructureModifiers(final Holder<Structure> structure, final List<StructureModifier> structureModifiers)
    {
        if (this.modifiedStructureInfo != null)
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Structure %s already modified", structure));

        StructureInfo original = this.getOriginalStructureInfo();
        final StructureInfo.Builder builder = StructureInfo.Builder.copyOf(original);
        for (StructureModifier.Phase phase : StructureModifier.Phase.values())
        {
            for (StructureModifier modifier : structureModifiers)
            {
                modifier.modify(structure, phase, builder);
            }
        }
        this.modifiedStructureInfo = builder.build();
    }
    
    /**
     * Record containing raw structure data.
     * @param structureSettings Structure settings.
     */
    public record StructureInfo(StructureSettings structureSettings)
    {
        public static class Builder
        {
            private StructureSettingsBuilder structureSettings;
            
            /**
             * @param original Original structure information
             * @return A ModifiedStructureInfo.StructureInfo.Builder with a copy of the structure's data
             */
            public static Builder copyOf(final StructureInfo original)
            {
                final StructureSettingsBuilder structureBuilder = StructureSettingsBuilder.copyOf(original.structureSettings());
                return new Builder(structureBuilder);
            }
            
            private Builder(final StructureSettingsBuilder structureSettings)
            {
                this.structureSettings = structureSettings;
            }

            public StructureInfo build()
            {
                return new StructureInfo(this.structureSettings.build());
            }

            public StructureSettingsBuilder getStructureSettings()
            {
                return structureSettings;
            }
        }
    }
}
