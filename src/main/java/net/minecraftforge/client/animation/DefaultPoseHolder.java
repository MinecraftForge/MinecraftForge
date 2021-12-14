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
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class DefaultPoseHolder<T extends Entity>
{
    private final ImmutableMap<ModelPart, PartPose> defaultPoseMap;

    public DefaultPoseHolder(EntityModel<T> entityModel)
    {
        this.defaultPoseMap = this.generatePoseMap(entityModel);
    }

    private ImmutableMap<ModelPart, PartPose> generatePoseMap(EntityModel<T> entityModel)
    {
        // Obviously it would be better if there was a common interface instead of this
        Map<ModelPart, PartPose> map = new HashMap<>();
        if (entityModel instanceof AgeableListModel<T> model)
        {
            model.allParts().forEach(part -> this.storeModelPoseAndSearch(map, part));
        }
        else if (entityModel instanceof HierarchicalModel<T> model)
        {
            this.storeModelPoseAndSearch(map, model.root());
        }
        else if (entityModel instanceof ListModel<T> model)
        {
            model.parts().forEach(part -> this.storeModelPoseAndSearch(map, part));
        }
        return ImmutableMap.copyOf(map);
    }

    private void storeModelPoseAndSearch(Map<ModelPart, PartPose> map, ModelPart part)
    {
        map.put(part, part.storePose());
        part.getAllParts().filter(p -> p != part).forEach(p -> this.storeModelPoseAndSearch(map, p));
    }

    public void restoreDefaultPose()
    {
        this.defaultPoseMap.forEach(ModelPart::loadPose);
    }
}
