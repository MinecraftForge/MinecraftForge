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

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * A class that stores a pose for an entire model
 */
public class ModelPoseHolder
{
    public static final ModelPoseHolder EMPTY = new ModelPoseHolder(Collections.emptyList());

    private final Reference2ReferenceMap<ModelPart, PartPose> defaultPoseMap;

    public ModelPoseHolder(ModelTree root)
    {
        this(root.children().stream().map(ModelTree::getPart).collect(Collectors.toList()));
    }

    public ModelPoseHolder(ModelPart root)
    {
        this(Collections.singleton(root));
    }

    public ModelPoseHolder(Iterable<ModelPart> parts)
    {
        this.defaultPoseMap = this.generatePoseMap(parts);
    }

    private Reference2ReferenceMap<ModelPart, PartPose> generatePoseMap(Iterable<ModelPart> parts)
    {
        Reference2ReferenceMap<ModelPart, PartPose> map = new Reference2ReferenceOpenHashMap<>();
        parts.forEach(part -> {
            map.put(part, part.storePose());
            part.getChildren().forEach((name, child) -> this.storeModelPoseAndSearch(map, child));
        });
        return Reference2ReferenceMaps.unmodifiable(map);
    }

    /**
     * Adds the given model part to the given map and searches it children.
     * <p>
     * <b>Note:</b> This method is recursive and exits based on a model part not having any children.
     *
     * @param map the map to put the model part and pose
     * @param part the model part to add and search
     */
    private void storeModelPoseAndSearch(Reference2ReferenceMap<ModelPart, PartPose> map, ModelPart part)
    {
        map.put(part, part.storePose());
        part.getAllParts().filter(p -> p != part).forEach(p -> this.storeModelPoseAndSearch(map, p));
    }

    /**
     * Restores the default pose of the entire model
     */
    public void restoreDefaultPose()
    {
        this.defaultPoseMap.forEach(ModelPart::loadPose);
    }

    /**
     * Gets the pose for the given model part. If the model part does not exist in the lookup map,
     * an empty part pose will be returned instead.
     *
     * @param part the model part to use for the lookup
     * @return the part pose of the model part otherwise an empty part pose if model part doesn't have a pose
     */
    @Nonnull
    public PartPose getPose(ModelPart part)
    {
        return this.defaultPoseMap.getOrDefault(part, PartPose.ZERO);
    }
}
