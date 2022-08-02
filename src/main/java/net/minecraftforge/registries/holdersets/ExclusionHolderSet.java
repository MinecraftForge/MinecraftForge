package net.minecraftforge.registries.holdersets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.ForgeMod;

/**
 * <p>Holderset that represents all elements that exist in one holderset but not another.
 * This is preferable over forge:not when the set of allowed elements is small relative to the size of the entire registry.
 * Json format:</p>
 * <pre>
 * {
 *   "type": "forge:exclusion",
 *   "include": "included_holderset", // string, list, or object
 *   "exclude": "included_holderset" // string, list, or object
 * }
 * </pre>
 */
public class ExclusionHolderSet<T> extends CompositeHolderSet<T>
{
    public static <T> Codec<? extends ICustomHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList)
    {
        Codec<HolderSet<T>> holderSetCodec = HolderSetCodec.create(registryKey, holderCodec, forceList);
        return RecordCodecBuilder.<ExclusionHolderSet<T>>create(builder -> builder.group(
                holderSetCodec.fieldOf("include").forGetter(ExclusionHolderSet::include),
                holderSetCodec.fieldOf("exclude").forGetter(ExclusionHolderSet::exclude)
            ).apply(builder, ExclusionHolderSet::new));
    }
    
    private final HolderSet<T> include;
    private final HolderSet<T> exclude;
    
    public HolderSet<T> include() { return this.include; }
    public HolderSet<T> exclude() { return this.exclude; }
    
    public ExclusionHolderSet(HolderSet<T> include, HolderSet<T> exclude)
    {
        super(List.of(include, exclude));
        this.include = include;
        this.exclude = exclude;
    }
    
    @Override
    public HolderSetType type()
    {
        return ForgeMod.EXCLUSION_HOLDER_SET.get();
    }

    @Override
    protected Set<Holder<T>> createSet()
    {
        return this.include.stream().filter(holder -> !this.exclude.contains(holder)).collect(Collectors.toSet());
    }
    
    @Override
    public String toString()
    {
        return "ExclusionSet{include=" + this.include + ", exclude=" + this.exclude + "}";
    }
}