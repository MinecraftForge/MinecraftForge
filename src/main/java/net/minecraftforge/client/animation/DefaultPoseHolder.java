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
import java.util.List;

/**
 * A class that stores a pose for an entire model
 */
public class DefaultPoseHolder
{
    private final List<Pair<ModelPart, PartPose>> defaultPoseList;

    public DefaultPoseHolder(ModelComponent root)
    {
        this.defaultPoseList = this.generatePoseList(root);
    }

    /**
     * Generates a map containing all parts of the model (including children) and the default pose
     * associated with them. Entries will only be added for select entity model classes since there
     * is no standard way to get all the model parts from an entity model.
     * <p>
     * The classes that are supported are:
     * <ul>
     *     <li>AgeableListModel</li>
     *     <li>HierarchicalModel</li>
     *     <li>ListModel</li>
     * </ul>
     *
     * @param root the root model component
     * @return a map containing all the default poses
     */
    private ImmutableList<Pair<ModelPart, PartPose>> generatePoseList(ModelComponent root)
    {
        // Obviously it would be better if there was a common interface instead of this
        List<Pair<ModelPart, PartPose>> list = new ArrayList<>();
        root.children().forEach((component) -> this.storeModelPoseAndSearch(list, component.getPart()));
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
