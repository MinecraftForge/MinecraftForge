package net.minecraftforge.common.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;
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
	
	public static final class AndHolderSet<T> implements HolderSet<T>
	{
		private final List<HolderSet<T>> values;
		
		public static <T> AndHolderSet<T> create(List<HolderSet<T>> values)
		{
			return new AndHolderSet<>(values);
		}
		
		private AndHolderSet(List<HolderSet<T>> values)
		{
			this.values = values;
		}

		@Override
		public Either<TagKey<T>, List<Holder<T>>> unwrap()
		{
			return Either.right(this.stream().toList());
		}

		@Override
		public boolean contains(Holder<T> holder)
		{
			for (HolderSet<T> holderSet : this.values)
			{
				if (!holderSet.contains(holder))
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public Iterator<Holder<T>> iterator()
		{
			return this.stream().iterator();
		}

		@Override
		public Stream<Holder<T>> stream()
		{
			return this.values.stream().flatMap(HolderSet::stream);
		}

		@Override
		public int size()
		{
			int size = 0;
			for (HolderSet<T> holderSet : this.values)
			{
				size += holderSet.size();
			}
			return size;
		}

		@Override
		public Optional<Holder<T>> getRandomElement(RandomSource random)
		{
			int size = this.size();
			if (size <= 0)
			{
				return Optional.empty();
			}
			return Optional.ofNullable(this.getNullable(size));
		}
		
		@Nullable
		public Holder<T> getNullable(int i)
		{
			for (HolderSet<T> subset : this.values)
			{
				int subsetSize = subset.size();
				if (i < subset.size())
				{
					return subset.get(i);
				}
				else
				{
					i -= subsetSize;
				}
			}
			return null;
		}

		@Override
		public Holder<T> get(int i)
		{
			@Nullable Holder<T> holder = this.getNullable(i);
			if (holder == null)
				throw new IndexOutOfBoundsException("Index " + i + " exceeds bounds " + this.size() + " of " + this.toString());
			return holder;
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
}
