/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.client.model;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;

public interface IForgeModel {

    //TODO: in 1.17 make this method abstract
    @SuppressWarnings("unchecked")
    public default Iterable<ModelRenderer> getModelParts(){
        return Collections.EMPTY_LIST;
    }
    
    public default Iterable<ModelBox> getModelBoxes() {
        ObjectList<ModelRenderer.ModelBox> boxList = new ObjectArrayList<>();
        for(ModelRenderer renderer : getModelParts()) {
            boxList.addAll(renderer.cubeList);
        }
        return boxList;
    }
    
    public default Iterable<Map.Entry<ModelRenderer, ModelBox>> getModelBoxesWithContext() {
        ArrayListMultimap<ModelRenderer, ModelBox> entries = ArrayListMultimap.create();
        for(ModelRenderer renderer : getModelParts()) {
            for(ModelBox box : renderer.cubeList) {
                entries.put(renderer, box);
            }
        }
        return entries.entries();
    }
}
