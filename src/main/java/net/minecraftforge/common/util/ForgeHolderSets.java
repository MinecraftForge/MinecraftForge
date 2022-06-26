package net.minecraftforge.common.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

public final class ForgeHolderSets
{
	private ForgeHolderSets() {} // Organizational class
	
	public static record AnyHolderSet<T>(Registry<T> registry) implements HolderSet<T>
	{
		public static <T> AnyHolderSet<T> create(Registry<T> registry)
		{
			return new AnyHolderSet<>(registry);
		}
		
		@Override
		public Iterator<Holder<T>> iterator()
        {
            return this.stream().iterator();
        }

		@Override
		public Stream<Holder<T>> stream()
		{
			return registry.holders().map(Function.identity());
		}

		@Override
		public int size()
		{
			return this.registry.size();
		}

		@Override
		public Either<TagKey<T>, List<Holder<T>>> unwrap()
		{
			return Either.right(this.stream().toList());
		}

		@Override
		public Optional<Holder<T>> getRandomElement(RandomSource random)
		{
			return this.registry.getRandom(random);
		}

		@Override
		public Holder<T> get(int i)
		{
			return this.registry.getHolder(i).orElseThrow(() -> new NoSuchElementException("No element " + i + " in registry " + this.registry.key()));
		}

		@Override
		public boolean contains(Holder<T> holder)
		{
			return holder.unwrapKey().map(this.registry::containsKey).orElse(false);
		}

		@Override
		public boolean isValidInRegistry(Registry<T> registry)
		{
			return this.registry == registry;
		}

		@Override
		public String toString()
		{
			return "AnyHolderSet(" + this.registry.key() + ")";
		}
	}
	
	public static record AndHolderSet<T>(List<HolderSet<T>> values) implements CompositeHolderSet<T>
	{		
		public static <T> AndHolderSet<T> create(List<HolderSet<T>> values)
		{
			return new AndHolderSet<>(values);
		}

        @Override
        public Stream<Holder<T>> stream()
        {
            final int subsetCount = this.values.size();
            if (subsetCount <= 0)
            {
                return Stream.empty();
            }
            return this.values.get(0).stream().filter(holder ->
            {
                final int size = this.values.size();
                for (int i=1; i<size; i++)
                {
                    if (!this.values.get(i).contains(holder))
                    {
                        return false;
                    }
                }
                return true;
            });
        }

		@Override
		public boolean contains(Holder<T> holder)
		{
			for (final HolderSet<T> subset : this.values)
			{
				if (!subset.contains(holder))
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean isValidInRegistry(Registry<T> registry)
		{
			for (HolderSet<T> subset : this.values)
			{
				if (!subset.isValidInRegistry(registry))
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString()
		{
			return "AndHolderSet[" + this.values + "]";
		}
	}
	
	public static record OrHolderSet<T>(List<HolderSet<T>> values) implements CompositeHolderSet<T>
	{
	    public static <T> OrHolderSet<T> create(List<HolderSet<T>> values)
	    {
	        return new OrHolderSet<>(values);
	    }

        @Override
        public Stream<Holder<T>> stream()
        {
            return this.values.stream().flatMap(HolderSet::stream).distinct();
        }

        @Override
        public boolean contains(Holder<T> holder)
        {
            for (HolderSet<T> subset : this.values)
            {
                if (subset.contains(holder))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isValidInRegistry(Registry<T> registry)
        {
            for (HolderSet<T> subset : this.values)
            {
                if (!subset.isValidInRegistry(registry))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString()
        {
            return "OrHolderSet[" + this.values + "]";
        }
	}
	
	public static record ExclusionHolderSet<T>(HolderSet<T> include, HolderSet<T> exclude) implements CompositeHolderSet<T>
	{
	    public static <T> ExclusionHolderSet<T> create(HolderSet<T> include, HolderSet<T> exclude)
	    {
	        return new ExclusionHolderSet<>(include, exclude);
	    }

        @Override
        public Stream<Holder<T>> stream()
        {
            return this.include.stream().filter(holder -> !exclude.contains(holder));
        }

        @Override
        public boolean contains(Holder<T> holder)
        {
            return this.include.contains(holder) && !this.exclude.contains(holder);
        }

        @Override
        public boolean isValidInRegistry(Registry<T> registry)
        {
            return this.include.isValidInRegistry(registry) && this.exclude.isValidInRegistry(registry);
        }

        @Override
        public String toString()
        {
            return "ExclusionHolderSet{include=" + this.include + ", exclude=" + this.exclude + "}";
        }
	}
    
	/**
	 * Represents a HolderSet composed of other HolderSets and defines some default common method implementations.
	 * This has some tricky requirements to fulfill as the sub-holdersets may
	 * be tag-based holdersets, which are mutable and can change-in-place during runtime.
	 * We cannot determine whether tags have changed, and so cannot cache our list of holders.
	 */
    public static interface CompositeHolderSet<T> extends HolderSet<T>
    {
        @Override
        default Iterator<Holder<T>> iterator()
        {
            return this.stream().iterator();
        }

        @Override
        default int size()
        {
            return (int)this.stream().count();
        }

        @Override
        default Either<TagKey<T>, List<Holder<T>>> unwrap()
        {
            return Either.right(this.stream().toList());
        }

        @Override
        default Optional<Holder<T>> getRandomElement(RandomSource random)
        {
            final List<Holder<T>> list = this.stream().toList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(random.nextInt(list.size())));
        }

        @Override
        default Holder<T> get(int i)
        {
            return this.stream().toList().get(i);
        }
    }
}
