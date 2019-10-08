/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.model.generators;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.util.ResourceLocation;

public class ItemModelBuilder extends ModelBuilder<ItemModelBuilder> {

    protected List<ItemOverride> overrides = new ArrayList<>();

    public ItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public OverrideBuilder override() {
        return this.new OverrideBuilder();
    }

    public class OverrideBuilder {

        private ResourceLocation model;
        private final Map<ResourceLocation, Float> predicates = new LinkedHashMap<>();

        public OverrideBuilder model(ResourceLocation model) {
            this.model = model;
            return this;
        }

        public OverrideBuilder predicate(ResourceLocation key, float value) {
            this.predicates.put(key, value);
            return this;
        }

        public ItemModelBuilder build() {
            ItemModelBuilder.this.overrides.add(new ItemOverride(model, predicates));
            return ItemModelBuilder.this;
        }
    }

}
