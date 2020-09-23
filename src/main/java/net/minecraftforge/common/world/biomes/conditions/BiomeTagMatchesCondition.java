package net.minecraftforge.common.world.biomes.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.world.biomes.ForgeBiomeModifiers;
import net.minecraftforge.common.world.biomes.conditions.base.BiomeConditionType;
import net.minecraftforge.common.world.biomes.conditions.base.IBiomeCondition;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeTagMatchesCondition implements IBiomeCondition
{
    public static final MapCodec<BiomeTagMatchesCondition> CODEC = ResourceLocation.field_240908_a_.xmap(BiomeTagMatchesCondition::new, cond -> cond.tagName).fieldOf("tag");

    public final ResourceLocation tagName;

    public BiomeTagMatchesCondition(ResourceLocation tagName)
    {
        this.tagName = tagName;
    }

    @Override
    public BiomeConditionType<?> getType()
    {
        return ForgeBiomeModifiers.MATCHES_TAG.get();
    }

    @Override
    public boolean test(Biome biome)
    {
        if(biome.getRegistryName() == null)
            return false;
        ITag<Biome> tag = TagCollectionManager.func_242178_a().getCustomTypeCollection(ForgeRegistries.BIOMES).get(tagName);
        if(tag == null)
            throw new RuntimeException("Tag " + tagName + " was not present for worldgen.");
        return tag.func_230236_b_().stream().anyMatch(b -> b.getRegistryName().equals(biome.getRegistryName()));
    }
}
