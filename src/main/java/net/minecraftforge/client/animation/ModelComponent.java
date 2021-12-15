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

package net.minecraftforge.client.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelPart;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModelComponent
{
    private static final ModelPart FALLBACK_MODEL_PART = new ModelPart(Collections.emptyList(), Collections.emptyMap());
    private static final ModelComponent FALLBACK_COMPONENT = new ModelComponent(FALLBACK_MODEL_PART);

    private final ModelPart part;
    private final Map<String, ModelComponent> children;

    public ModelComponent(IAnimatedModel model)
    {
        this.part = FALLBACK_MODEL_PART;
        this.children = this.generateComponentMap(model);
    }

    private ModelComponent(ModelPart part)
    {
        this.part = part;
        this.children = this.generateComponentMap(part);
    }

    private Map<String, ModelComponent> generateComponentMap(IAnimatedModel model)
    {
        Map<String, ModelPart> partMap = new HashMap<>();
        model.gatherAnimatedParts(partMap);
        return this.generateComponentMap(partMap);
    }

    private Map<String, ModelComponent> generateComponentMap(ModelPart root)
    {
        return this.generateComponentMap(root.getChildren());
    }

    private Map<String, ModelComponent> generateComponentMap(Map<String, ModelPart> parts)
    {
        Map<String, ModelComponent> map = new HashMap<>();
        parts.forEach((name, part) -> map.put(name, new ModelComponent(part)));
        return ImmutableMap.copyOf(map);
    }

    public ModelPart asPart()
    {
        return this.part;
    }

    @Nonnull
    public ModelComponent get(String name)
    {
        return this.children.getOrDefault(name, FALLBACK_COMPONENT);
    }

    Collection<ModelComponent> children()
    {
        return this.children.values();
    }

    ///////////////////////////////////////////////////////
    // Maybe a method to dump the structure for modders? //
    ///////////////////////////////////////////////////////
}
