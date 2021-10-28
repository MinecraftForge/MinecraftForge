/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;

import java.util.List;

/**
 * Implementation that defines what a structure pool modifier must implement in order to work.<br/>
 * Requires a {@link StructurePoolModifierSerializer} to be registered via json (see forge:structure_pool_modifiers/structure_pool_modifiers).
 */
public interface IStructurePoolModifier
{

    /**
     * Applies the modifier to the list of default structure pools.
     * This function should check the given ResourceLocation to selectively apply modifiers.
     *
     * @param name     the ResourceLocation of the StructureTemplatePool currently being processed
     * @param elements the resulting list of StructurePoolElements, which can have elements added & removed
     */
    void apply(ResourceLocation name, List<StructurePoolElement> elements);

}
