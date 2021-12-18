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

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that stores a pose for an entire model
 */
public class ModelPoseHolder
{
    private final List<Pair<ModelPart, PartPose>> defaultPoseList;

    public ModelPoseHolder(ModelTree root)
    {
        this.defaultPoseList = this.generatePoseList(root.children().stream().map(ModelTree::getPart).collect(Collectors.toList()));
    }

    public ModelPoseHolder(ModelPart root)
    {
        this.defaultPoseList = this.generatePoseList(Collections.singleton(root));
    }

    public ModelPoseHolder(Iterable<ModelPart> parts)
    {
        this.defaultPoseList = this.generatePoseList(parts);
    }

    /**
     * Generates a list pair of all the animated model parts (including children) of a model and the
     * default pose associated with them.
     *
     * @param root the root model component
     * @return a map containing all the default poses
     */
    private ImmutableList<Pair<ModelPart, PartPose>> generatePoseList(ModelTree root)
    {
        // Obviously it would be better if there was a common interface instead of this
        List<Pair<ModelPart, PartPose>> list = new ArrayList<>();
        root.children().forEach((component) -> this.storeModelPoseAndSearch(list, component.getPart()));
        return ImmutableList.copyOf(list);
    }

    private ImmutableList<Pair<ModelPart, PartPose>> generatePoseList(Iterable<ModelPart> parts)
    {
        // Obviously it would be better if there was a common interface instead of this
        List<Pair<ModelPart, PartPose>> list = new ArrayList<>();
        parts.forEach(part -> {
            list.add(Pair.of(part, part.storePose()));
            part.getChildren().forEach((name, child) -> this.storeModelPoseAndSearch(list, child));
        });
        return ImmutableList.copyOf(list);
    }

    /**
     * Adds the given model part to the given map and searches it children.
     * <p>
     * <b>Note:</b> This method is recursive and exits based on a model part not having any children.
     *
     * @param list the list to add the model part and pose pair
     * @param part the model part to add and search
     */
    private void storeModelPoseAndSearch(List<Pair<ModelPart, PartPose>> list, ModelPart part)
    {
        list.add(Pair.of(part, part.storePose()));
        part.getAllParts().filter(p -> p != part).forEach(p -> this.storeModelPoseAndSearch(list, p));
    }

    /**
     * Restores the default pose
     */
    public void restoreDefaultPose()
    {
        this.defaultPoseList.forEach(pair -> pair.getLeft().loadPose(pair.getRight()));
    }
}