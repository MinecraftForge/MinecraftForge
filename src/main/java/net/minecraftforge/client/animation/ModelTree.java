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

public class ModelTree
{
    private static final ModelPart FALLBACK_MODEL_PART = new ModelPart(Collections.emptyList(), Collections.emptyMap());
    private static final ModelTree FALLBACK_COMPONENT = new ModelTree(FALLBACK_MODEL_PART);

    private final ModelPart part;
    private final Map<String, ModelTree> children;

    public ModelTree(IAnimatedModel model)
    {
        this.part = FALLBACK_MODEL_PART;
        this.children = this.generateComponentMap(model);
    }

    private ModelTree(ModelPart part)
    {
        this.part = part;
        this.children = this.generateComponentMap(part);
    }

    private Map<String, ModelTree> generateComponentMap(IAnimatedModel model)
    {
        Map<String, ModelPart> partMap = new HashMap<>();
        model.gatherAnimatedParts(partMap::put);
        return this.generateComponentMap(partMap);
    }

    private Map<String, ModelTree> generateComponentMap(ModelPart root)
    {
        return this.generateComponentMap(root.getChildren());
    }

    private Map<String, ModelTree> generateComponentMap(Map<String, ModelPart> parts)
    {
        Map<String, ModelTree> map = new HashMap<>();
        parts.forEach((name, part) -> map.put(name, new ModelTree(part)));
        return ImmutableMap.copyOf(map);
    }

    /**
     * Looks up and retrieves a model part for the given path. It should be noted that if no model
     * part exist at the specified path, a fallback model will be returned. Editing the fallback
     * model part will have no effect on the model animation. This method returns non-null for the
     * convenience of not having to constantly null check before using model parts.
     *
     * @param path the path of the model part
     * @return a model part from the model or a fallback model if no model part existed at the path
     */
    @Nonnull
    public ModelPart get(String path)
    {
        String[] splitPath = path.split("\\.");
        ModelTree c = this;
        for(String s : splitPath)
        {
            c = c.children.getOrDefault(s, FALLBACK_COMPONENT);
        }
        return c.part;
    }

    ModelPart getPart()
    {
        return this.part;
    }

    Collection<ModelTree> children()
    {
        return this.children.values();
    }

    ///////////////////////////////////////////////////////
    // Maybe a method to dump the structure for modders? //
    ///////////////////////////////////////////////////////
}
