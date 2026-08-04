/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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

        @Deprecated
        public ExistingModelFile(String location, ExistingFileHelper existingHelper) {
            this(new ResourceLocation(location), existingHelper);
        }

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
