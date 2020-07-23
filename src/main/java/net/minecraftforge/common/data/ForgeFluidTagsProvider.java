package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;

import static net.minecraftforge.common.Tags.Fluids.FLUID;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgeFluidTagsProvider extends FluidTagsProvider
{
    private Set<ResourceLocation> filter = null;

    public ForgeFluidTagsProvider(DataGenerator gen)
    {
        super(gen);
    }

    @Override
    public void registerTags()
    {
        super.registerTags();
        filter = this.tagToBuilder.entrySet().stream().map(e -> e.getKey().getId()).collect(Collectors.toSet());

        getBuilder(FLUID).add(Fluids.WATER, Fluids.FLOWING_WATER, Fluids.LAVA, Fluids.FLOWING_LAVA);
    }

    @Override
    protected Path makePath(ResourceLocation id)
    {
        return filter != null && filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
    }

    @Override
    public String getName()
    {
        return "Forge Fluid Tags";
    }
}
