package net.minecraftforge.common.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;

import java.util.List;

/**
 * Implementation that defines what a structure pool modifier must implement in order to work.<br/>
 * Requires a {@link StructurePoolModifierSerializer} to be registered via json (see forge:structure_pool_modifiers/structure_pool_modifiers).
 */
public interface IStructurePoolModifier {

    /**
     * Applies the modifier to the list of default structure pools.
     * This function should check the given ResourceLocation to selectively apply modifiers.
     * @param name the ResourceLocation of the StructureTemplatePool currently being processed
     * @param elements the resulting list of StructurePoolElements, which can have elements added & removed
     */
    void apply(ResourceLocation name, List<StructurePoolElement> elements);

}
