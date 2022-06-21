/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;


import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.ICompleteCapabilityProvider;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.ICopyableCapabilityProvider;
import net.minecraftforge.common.capabilities.IAttachedCapabilityProvider.IComparableCapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.eventbus.api.Event;

/**
 * The AttachCapabilitiesEvent is used to attach {@link IAttachedCapabilityProvider}s to game objects.<br>
 * The firing of this event is determined by the provider object, and is not consistent across usages.<br>
 * For most cases, this is fired whenever the {@link CapabilityDispatcher} must be resolved.<br>
 * This means that typically, this event is not fired until capabilities are attempted to be accessed for a particular object.
 */
public abstract class AttachCapabilitiesEvent<T extends ICapabilityProvider> extends Event
{
    protected final T obj;
    protected final Map<CapabilityType<?>, IAttachedCapabilityProvider<T>> providers = new IdentityHashMap<>(); // Type objects can be compared using ==
    protected final Map<CapabilityType<?>, IAttachedCapabilityProvider<T>> view = Collections.unmodifiableMap(providers);
    protected final Map<ResourceLocation, IAttachedCapabilityProvider<T>> byName = new HashMap<>();
    protected final Map<ResourceLocation, IAttachedCapabilityProvider<T>> byNameView = Collections.unmodifiableMap(byName);

    protected AttachCapabilitiesEvent(T obj)
    {
        this.obj = obj;
    }

    /**
     * Returns the object that is having capabilities attached.
     */
    public final T getObject()
    {
        return this.obj;
    }

    /**
     * Returns the number of successful attachments.
     */
    public final int getSize()
    {
        return this.providers.size();
    }

    /**
     * Adds a {@link IAttachedCapabilityProvider} to this object.<br>
     * Only one provider may exist for any given {@link CapabilityType}, on a first-come-first-serve basis.<br>
     * The same provider object may be attached to multiple capability types.<br>
     * Separate provider objects must have separate IDs.<br>
     * 
     * @param provider The provider being attached to this game object.
     * @return true, if this provider was successfully attached, otherwise false.
     * @throws UnsupportedOperationException If a different provider with the same ID already exists.
     * @see {@link #builder(CapabilityType...)}
     */
    public boolean addCapability(CapabilityType<?> type, IAttachedCapabilityProvider<T> provider)
    {
        if(type == null || provider == null) throw new UnsupportedOperationException("Attempted to attach with either a null provider or null type!");
        var current = this.byName.get(provider.getId());
        if(current != null && current != provider) throw new UnsupportedOperationException(String.format("Attempted to attach a provider to %s with ID %s, but that ID is already in use!", obj.toString(), provider.getId()));
        if(this.providers.containsKey(type)) return false;
        this.providers.put(type, provider);
        this.byName.put(provider.getId(), provider);
        return true;
    }

    /**
     * A read-only view of the type -> provider map.
     */
    public Map<CapabilityType<?>, IAttachedCapabilityProvider<T>> getCapabilities()
    {
        return this.view;
    }

    /**
     * A read-only view of the ID -> provider map.
     */
    public Map<ResourceLocation, IAttachedCapabilityProvider<T>> getCapabilitiesByName()
    {
        return this.byNameView;
    }

    protected void validate(ProviderBuilder<?> builder)
    {
        Preconditions.checkNotNull(builder.id, "A Provider cannot be built without an ID!");
        Preconditions.checkNotNull(builder.memoizedData, "A Provider cannot be built without data supplier!");
        Preconditions.checkArgument(!builder.types.isEmpty(), "A Provider cannot be built without any specified Capability Types!");
    }

    /**
     * This method allows you to use a builder to create a provider for a single object, instead of having to
     * implement all the methods of {@link IAttachedCapabilityProvider} or subclasses.<br>
     * You still need to provide all functionality required by the game object being attached to.
     * <p>
     * An example of attaching an ItemStackHandler to an ItemStack is shown below:<br>
     * <pre>
     * event.<ItemStackHandler>builder(CapabilityTypes.ITEMS)
     *     .supplies(() -> new ItemStackHandler())
     *     .serializesWith(ItemStackHandler::serializeNBT, ItemStackHandler::deserializeNBT)
     *     .comparesWith(MyClass::areHandlersEqual)
     *     .copiesWith(MyClass::cloneHandler)
     *     .attach(new ResourceLocation("test", "items"));
     * </pre>
     * <code>serializesWith</code> may be omitted if you do not want your cap to save to disk.<br>
     * <code>copiesWith</code> is required for ItemStacks and Players.<br>
     * <code>comparesWith</code> is required for ItemStacks.
     * <p>
     * You must specify the generic type explicitly during this method call for it to work correctly.
     * @param <D> The type of object being attached.
     * @param types The CapabilityTypes that the attached object provides.
     * @return A new {@link ProviderBuilder} of the specified type.
     */
    @SafeVarargs
    public final <D> ProviderBuilder<D> builder(CapabilityType<? super D>... types)
    {
        return new ProviderBuilder<>(types);
    }

    /**
     * A Builder class for creating {@link ICompleteCapabilityProvider}s.
     * @param <D> The type of object being provided.
     */
    public class ProviderBuilder<D>
    {
        private ResourceLocation id;
        private Supplier<D> memoizedData;
        private Set<CapabilityType<? super D>> types = Collections.newSetFromMap(new IdentityHashMap<>());
        private Function<D, CompoundTag> serializer;
        private BiConsumer<D, CompoundTag> deserializer;
        private BiFunction<D, T, D> copier;
        private BiPredicate<D, D> comparator;
        
        @SafeVarargs
        private ProviderBuilder(CapabilityType<? super D>... types)
        {
            for(CapabilityType<? super D> t : types) this.types.add(t);
        }

        /**
         * Specify a data supplier for this provider.
         * The supplier may not return null, and will be resolved exactly once, when necessary.
         * @param supplier The data supplier
         * @return <code>this</code>
         */
        public final ProviderBuilder<D> supplies(Supplier<D> supplier)
        {
            this.memoizedData = Suppliers.memoize(supplier);
            return this;
        }

        /**
         * Specify a serializer and deserializer for this provider.<br>
         * If your object implements {@link INBTSerializable}, then you can use the following example:<br>
         * <pre>.serializesWith(INBTSerializable::serializeNBT, INBTSerializable::deserializeNBT)</pre>
         * @param serializer The object's serializer.
         * @param deserializer The object's deserializer.
         * @return <code>this</code>
         * @see {@link IAttachedCapabilityProvider#serializeNBT()}
         * @see {@link IAttachedCapabilityProvider#deserializeNBT(CompoundTag)}
         */
        public ProviderBuilder<D> serializesWith(Function<D, CompoundTag> serializer, BiConsumer<D, CompoundTag> deserializer)
        {
            this.serializer = serializer;
            this.deserializer = deserializer;
            return this;
        }

        /**
         * Specify a copy function for this provider.<br>
         * The result of the function should be a <b><u>deep copy</u></b>, as shallow references may interfere
         * with the original.
         * @param copier The copy function.
         * @return <code>this</code>
         * @see {@link ICopyableCapabilityProvider#copy(ICapabilityProvider)0}
         */
        public ProviderBuilder<D> copiesWith(BiFunction<D, T, D> copier)
        {
            this.copier = copier;
            return this;
        }

        /**
         * Specify a comparator function for this provider.<br>
         * This is used when checking if two instances of this provider are equal, for things like itemstack merging.<br>
         * If the two are equal, the function should return <code>true</code>.<br>
         * If this function states that two instances are equal, it means that one could replace the other with no impact.
         * @param copier The comparison function.
         * @return <code>this</code>
         * @see {@link IComparableCapabilityProvider#isEquivalentTo(IComparableCapabilityProvider)}
         */
        public ProviderBuilder<D> comparesWith(BiPredicate<D, D> comparator)
        {
            this.comparator = comparator;
            return this;
        }

        /**
         * Validates, builds, and attaches the provider to all specified capability types.<br>
         * This method has weak error resolution, meaning any failed attachments will be ignored.<br>
         * If you need strong error handling, use {@link #build(ResourceLocation)} and 
         * {@link AttachCapabilitiesEvent#addCapability(CapabilityType, IAttachedCapabilityProvider)} directly.
         * @param id The ID of this provider.
         */
        public void attach(ResourceLocation id)
        {
        	ICompleteCapabilityProvider<T> prov = build(id);
            types.forEach(t -> AttachCapabilitiesEvent.this.addCapability(t, prov));
        }

        /**
         * Validates and builds the provider as specified by this builder.<br>
         * If using this method, you will need to manually attach to all specified capability types through 
         * {@link AttachCapabilitiesEvent#addCapability(CapabilityType, IAttachedCapabilityProvider)}
         * @param id The ID of this provider.
         * @return The built provider.
         * @see {@link #attach(ResourceLocation)}
         */
        public ICompleteCapabilityProvider<T> build(ResourceLocation id)
        {
        	this.id = id;
        	validate(this);
        	return new BuiltProvider<>(this);
        }
    }

    /**
     * An attached capability provider that was constructed by {@link ProviderBuilder}.
     *
     * @param <T> The type of object being attached to.
     * @param <D> The type of object being provided.
     */
    private static class BuiltProvider<T extends ICapabilityProvider, D> implements ICompleteCapabilityProvider<T>
    {
        private final ResourceLocation id;
        private final Supplier<D> memoizedData;
        private final Set<CapabilityType<? super D>> types;
        private final Function<D, CompoundTag> serializer;
        private final BiConsumer<D, CompoundTag> deserializer;
        private final BiFunction<D, T, D> copier;
        private final BiPredicate<D, D> comparator;
        private Capability<D> cap;

        BuiltProvider(AttachCapabilitiesEvent<T>.ProviderBuilder<D> builder)
        {
            this.id = builder.id;
            this.memoizedData = builder.memoizedData;
            this.types = builder.types;
            this.serializer = builder.serializer == null ? d -> null : builder.serializer;
            this.deserializer = builder.deserializer == null ? (d, tag) -> {} : builder.deserializer;
            this.copier = builder.copier == null ? (d, t) -> null : builder.copier;
            this.comparator = builder.comparator == null ? (a, b) -> true : builder.comparator;
            this.cap = Capability.of(() -> this.memoizedData.get());
        }

        BuiltProvider(BuiltProvider<T, D> other, T copiedParent)
        {
            this.id = other.id;
            this.types = other.types;
            this.serializer = other.serializer;
            this.deserializer = other.deserializer;
            this.copier = other.copier;
            this.comparator = other.comparator;
            D copy = this.copier.apply(other.memoizedData.get(), copiedParent);
            this.memoizedData = Suppliers.memoize(() -> copy); // Immediately copy to avoid capturing a reference to "other"
            this.cap = Capability.of(() -> this.memoizedData.get());
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean isEquivalentTo(@Nullable IComparableCapabilityProvider<T> other)
        {
            return this.comparator.test(memoizedData.get(), ((BuiltProvider<T, D>) other).memoizedData.get());
        }

        @Override
        public @Nullable CompoundTag serializeNBT()
        {
            return this.serializer.apply(this.memoizedData.get());
        }

        @Override
        public void deserializeNBT(CompoundTag tag)
        {
            this.deserializer.accept(this.memoizedData.get(), tag);
        }

        @Override
        public ResourceLocation getId()
        {
            return this.id;
        }

        @Override
        public <C> @NotNull Capability<C> getCapability(CapabilityType<C> type, @Nullable Direction direction)
        {
            if(this.types.contains(type)) return this.cap.cast();
            return Capability.empty();
        }

        @Override
        public void invalidateCaps()
        {
            this.cap.invalidate();
        }

        @Override
        public void reviveCaps()
        {
            this.cap = Capability.of(() -> memoizedData.get());
        }

        @Override
        public @Nullable ICopyableCapabilityProvider<T> copy(T copiedParent)
        {
            return new BuiltProvider<>(this, copiedParent);
        }
    }

    /**
     * This subclass is abnormal because it requires {@link #addCapability} to take an
     * {@link ICompleteCapabilityProvider} instead of an {@link IAttachedCapabilityProvider}.
     *
     */
    public static final class ItemStacks extends AttachCapabilitiesEvent<ItemStack>
    {

        public ItemStacks(ItemStack obj)
        {
            super(obj);
        }

        /**
         * Providers attached to {@link ItemStack} must implement {@link ICompleteCapabilityProvider}.
         * @see {@link ItemStacks#addCapability(CapabilityType, ICompleteCapabilityProvider)}
         */
        @Override
        @Deprecated
        public boolean addCapability(CapabilityType<?> type, IAttachedCapabilityProvider<ItemStack> provider)
        {
            Preconditions.checkArgument(provider instanceof ICompleteCapabilityProvider, "Providers attached to ItemStack must implement ICompleteCapabilityProvider!");
            return addCapability(type, (ICompleteCapabilityProvider<ItemStack>) provider);
        }

        /**
         * @see {@link AttachCapabilitiesEvent#addCapability(IAttachedCapabilityProvider)}
         */
        public boolean addCapability(CapabilityType<?> type, ICompleteCapabilityProvider<ItemStack> provider)
        {
            return super.addCapability(type, provider);
        }
        
        @Override
        protected void validate(ProviderBuilder<?> builder)
        {
            super.validate(builder);
            Preconditions.checkNotNull(builder.comparator, "A Provider cannot be built for ItemStacks without a comparator!");
            Preconditions.checkNotNull(builder.copier, "A Provider cannot be built for ItemStacks without a copy function!");
        }

    }

    public static final class BlockEntities extends AttachCapabilitiesEvent<BlockEntity>
    {

        public BlockEntities(BlockEntity obj)
        {
            super(obj);
        }

    }

    public static final class Chunks extends AttachCapabilitiesEvent<LevelChunk>
    {

        public Chunks(LevelChunk obj)
        {
            super(obj);
        }

    }

    public static final class Entities extends AttachCapabilitiesEvent<Entity>
    {

        public Entities(Entity obj)
        {
            super(obj);
        }

    }

    public static final class Levels extends AttachCapabilitiesEvent<Level>
    {

        public Levels(Level obj)
        {
            super(obj);
        }

    }

    /**
     * This subclass is abnormal because it requires {@link #addCapability} to take an
     * {@link ICopyableCapabilityProvider} instead of an {@link IAttachedCapabilityProvider}.
     *
     */
    public static final class Players extends AttachCapabilitiesEvent<Player>
    {

        public Players(Player obj)
        {
            super(obj);
        }

        /**
         * Providers attached to {@link Player} must implement {@link ICopyableCapabilityProvider}.
         * @see {@link Players#addCapability(CapabilityType, ICopyableCapabilityProvider)}
         */
        @Override
        @Deprecated
        public boolean addCapability(CapabilityType<?> type, IAttachedCapabilityProvider<Player> provider)
        {
            Preconditions.checkArgument(provider instanceof ICopyableCapabilityProvider, "Providers attached to Player must implement ICopyableCapabilityProvider!");
            return addCapability(type, (ICopyableCapabilityProvider<Player>) provider);
        }

        /**
         * @see {@link AttachCapabilitiesEvent#addCapability(IAttachedCapabilityProvider)}
         */
        public boolean addCapability(CapabilityType<?> type, ICopyableCapabilityProvider<Player> provider)
        {
            return super.addCapability(type, provider);
        }

        @Override
        protected void validate(ProviderBuilder<?> builder)
        {
            super.validate(builder);
            Preconditions.checkNotNull(builder.copier, "A Provider cannot be built for Players without a comparator!");
        }
    }
}