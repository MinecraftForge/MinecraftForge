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

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;

import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

public abstract class ModelFile {

    protected ResourceLocation location;

    protected ModelFile(ResourceLocation location) {
        this.location = location;
    }

    protected abstract boolean exists();

    public ResourceLocation getLocation() {
        assertExistence();
        return location;
    }

    /**
     * Assert that this model exists.
     * @throws IllegalStateException if this model does not exist
     */
    public void assertExistence() {
        Preconditions.checkState(exists(), "Model at %s does not exist", location);
    }

    public ResourceLocation getUncheckedLocation() {
        return location;
    }

    public static class UncheckedModelFile extends ModelFile {

        public UncheckedModelFile(String location) {
           this(new ResourceLocation(location));
        }
        public UncheckedModelFile(ResourceLocation location) {
            super(location);
        }

        @Override
        protected boolean exists() {
            return true;
        }
    }

    public static class ExistingModelFile extends ModelFile {
        private final ExistingFileHelper existingHelper;

        public ExistingModelFile(ResourceLocation location, ExistingFileHelper existingHelper) {
            super(location);
            this.existingHelper = existingHelper;
        }

        @Override
        protected boolean exists() {
            if (getUncheckedLocation().getPath().contains("."))
                return existingHelper.exists(getUncheckedLocation(), ResourcePackType.CLIENT_RESOURCES, "", "models");
            else
                return existingHelper.exists(getUncheckedLocation(), ResourcePackType.CLIENT_RESOURCES, ".json", "models");
        }
    }
}
