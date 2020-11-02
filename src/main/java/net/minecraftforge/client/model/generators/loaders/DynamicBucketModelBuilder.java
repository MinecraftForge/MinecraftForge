package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
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
