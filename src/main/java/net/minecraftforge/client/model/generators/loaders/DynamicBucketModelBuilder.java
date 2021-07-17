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

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DynamicBucketModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    public static <T extends ModelBuilder<T>> DynamicBucketModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new DynamicBucketModelBuilder<>(parent, existingFileHelper);
    }

    private ResourceLocation fluid;
    private Boolean flipGas;
    private Boolean applyTint;
    private Boolean coverIsMask;
    private Boolean applyFluidLuminosity;

    protected DynamicBucketModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(new ResourceLocation("forge:bucket"), parent, existingFileHelper);
    }

    public DynamicBucketModelBuilder<T> fluid(Fluid fluid)
    {
        Preconditions.checkNotNull(fluid, "fluid must not be null");
        this.fluid = fluid.getRegistryName();
        return this;
    }

    public DynamicBucketModelBuilder<T> flipGas(boolean flip)
    {
        this.flipGas = flip;
        return this;
    }

    public DynamicBucketModelBuilder<T> applyTint(boolean tint)
    {
        this.applyTint = tint;
        return this;
    }

    public DynamicBucketModelBuilder<T> coverIsMask(boolean coverIsMask)
    {
        this.coverIsMask = coverIsMask;
        return this;
    }

    public DynamicBucketModelBuilder<T> applyFluidLuminosity(boolean applyFluidLuminosity)
    {
        this.applyFluidLuminosity = applyFluidLuminosity;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        Preconditions.checkNotNull(fluid, "fluid must not be null");

        json.addProperty("fluid", fluid.toString());

        if (flipGas != null)
            json.addProperty("flipGas", flipGas);

        if (applyTint != null)
            json.addProperty("applyTint", applyTint);

        if (coverIsMask != null)
            json.addProperty("coverIsMask", coverIsMask);

        if (applyFluidLuminosity != null)
            json.addProperty("applyFluidLuminosity", applyFluidLuminosity);

        return json;
    }
}
