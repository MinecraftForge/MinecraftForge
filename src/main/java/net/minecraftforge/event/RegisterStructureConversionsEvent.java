/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fired for registering structure conversions for pre-1.18.2 worlds. This is used by {@link StructuresBecomeConfiguredFix}
 * for converting old structure IDs in pre-1.18.2 worlds to their new equivalents, which can be differentiated per biome.
 *
 * <p>By default, structures whose old ID has a namespace which is not equal to {@value ResourceLocation#DEFAULT_NAMESPACE}
 * will be assumed to belong to a modded structure and will be used as the new ID. Mods may choose to register structure
 * conversions for their structures, if they wish to override this default behavior.</p>
 *
 * <p>This event will only fire if {@link StructuresBecomeConfiguredFix} is used, as a result of converting a
 * pre-1.18.2 world to the current version.</p>
 *
 * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide#SERVER logical server}. </p>
 *
 * @see StructuresBecomeConfiguredFix
 * @see #register(String, StructuresBecomeConfiguredFix.Conversion)
 */
public class RegisterStructureConversionsEvent extends Event
{
    private final Map<String, StructuresBecomeConfiguredFix.Conversion> map;

    /**
     * @hidden For internal use only.
     */
    public RegisterStructureConversionsEvent(Map<String, StructuresBecomeConfiguredFix.Conversion> map)
    {
        this.map = map;
    }

    /**
     * Registers a conversion for a structure.
     *
     * <p>A structure conversion can be of two kinds:</p>
     * <ul>
     *     <li>A <em>trivial</em> conversion, created using {@link StructuresBecomeConfiguredFix.Conversion#trivial(String)},
     *     contains only the new structure ID and simply converts all mentions of the old structure ID to the new
     *     structure ID.</li>
     *     <li>A <em>biome-mapped</em> conversion, created using {@link
     *     StructuresBecomeConfiguredFix.Conversion#biomeMapped(Map, String)}, contains a fallback structure ID, and a
     *     biome-specific conversion map. Each entry in the map is composed of a list of biome IDs and the new structure
     *     ID.
     *
     *     <p>If a structure is in a biome which exists in the map, the
     *     structure ID in the corresponding entry is used as the new structure ID. If there is no such biome found,
     *     the new structure ID will be the fallback structure ID.</p>
     *
     *     <p>For example, the following registers a biome-mapped conversion for {@code exampleStructure} with the
     *     following logic:</p>
     *     <ul>
     *         <li>If the structure is within either a {@code minecraft:desert} or a {@code minecraft:jungle} biome,
     *         it is mapped to {@code examplemod:deserted_structure}.</li>
     *         <li>If the structure is within a {@code minecraft:ocean} biome, it is mapped to
     *         {@code examplemod:flooded_structure}.</li>
     *         <li>Otherwise, the structure is mapped to {@code examplemod:structure}.</li>
     *     </ul>
     *     <pre>{@code
     * event.register("exampleStructure", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(
     *     List.of("minecraft:desert", "minecraft:jungle"), "examplemod:deserted_structure",
     *     List.of("minecraft:ocean"), "examplemod:flooded_structure"
     * ), "examplemod:structure"));
     *     }</pre>
     *     </li>
     * </ul>
     *
     * @param oldStructureID the old structure ID, in all lowercase
     * @param conversion     the conversion data
     * @throws NullPointerException     if the old structure ID, the conversion data, or the fallback structure ID in
     *                                  the conversion data is null
     * @throws IllegalArgumentException if the old structure ID is not in full lowercase, or if a conversion for that
     *                                  structure ID has already been registered previously
     */
    public void register(String oldStructureID, StructuresBecomeConfiguredFix.Conversion conversion)
    {
        checkNotNull(oldStructureID, "Original structure ID must not be null");
        checkArgument(oldStructureID.toLowerCase(Locale.ROOT).equals(oldStructureID),
                "Original structure ID should be in all lowercase");
        checkNotNull(conversion, "Structure conversion must not be null");
        checkNotNull(conversion.fallback(), "Fallback structure ID in structure conversion must not be null");
        if (map.putIfAbsent(oldStructureID.toLowerCase(Locale.ROOT), conversion) != null)
        {
            throw new IllegalArgumentException("Conversion has already been registered for structure " + oldStructureID);
        }
    }
}
