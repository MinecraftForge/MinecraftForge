/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Utility class to help with managing registry entries.
 * Maintains a list of all suppliers for entries and registers them during the proper Register event.
 * Suppliers should return NEW instances every time.
 *
 *Example Usage:
 *<pre>
 *   private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
 *   private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
 *
 *   public static final RegistryObject<Block> ROCK_BLOCK = BLOCKS.register("rock", () -> new Block(Block.Properties.create(Material.ROCK)));
 *   public static final RegistryObject<Item> ROCK_ITEM = ITEMS.register("rock", () -> new BlockItem(ROCK_BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
 *
 *   public ExampleMod() {
 *       ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *       BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
 *   }
 *</pre>
 *
 * @param <T> The base registry type, must be a concrete base class, do not use subclasses or wild cards.
 */
public class DeferredRegister<T extends IForgeRegistryEntry<T>>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("DeferredRegister");
    private IForgeRegistry<T> type;
    private NonNullSupplier<IForgeRegistry<T>> typeSup;
    private final String modid;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private final Map<LazyRegistryObject<T>, Supplier<? extends T>> lazyEntries = new LinkedHashMap<>();
    //private final Set<LazyRegistryObject<T>> lazyEntriesView = Collections.unmodifiableSet(lazyEntries.keySet());

    public DeferredRegister(IForgeRegistry<T> reg, String modid)
    {
        Objects.requireNonNull(reg, "The registry must not be null! For custom registries use the supplier version instead.");
        this.type = reg;
        this.modid = modid;
    }

    /**
     * Custom registries are only created after the {@link DeferredRegister} constructor is called due to static init.
     * To solve this, the {@link IForgeRegistry} and {@link RegistryObject}s must be evaluated only when registry events are fired.
     * The passed in supplier will be called automatically when {@link #addEntries} is invoked.
     * When that happens, it is guaranteed that the registry is created since the {@link RegistryEvent.NewRegistry} event is fired beforehand.
     *
     * However, this can mess up if modders call {@link #getEntries()} too quickly. It can be called only after the NewRegistry has fired.
     * Calling it then won't crash, but will result in empty RegistryObject that will update when the appropriate RegistryEvent is fired.
     *
     * @param supReg A nonnull supplier of an IForgeRegistry of the desired type.
     */
    public DeferredRegister(NonNullSupplier<IForgeRegistry<T>> supReg, String modid)
    {
        Objects.requireNonNull(supReg);
        this.typeSup = supReg;
        this.modid = modid;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated when the entries in the registry change.
     */
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        final ResourceLocation key = new ResourceLocation(modid, name);
        if(this.type == null)
        {
            LOGGER.error("Errored while trying to register an entry. This likely happened due to registering for a custom registry, use lazyRegister instead.");
            throw new IllegalArgumentException("Null registry used in DeferredRegister");
        }
        RegistryObject<I> ret = RegistryObject.of(key, this.type);
        if (entries.putIfAbsent((RegistryObject<T>) ret, () -> sup.get().setRegistryName(key)) != null) {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }
        return ret;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a {@link LazyRegistryObject}
     * that will be populated with the created entry automatically and lazily.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup A factory for the new entry, it should return a new instance every time it is called.
     * @return A {@link LazyRegistryObject} that will be lazily updated when the entries in the registry change.
     */
    public <I extends T> LazyRegistryObject<I> lazyRegister(final String name, final Supplier<? extends I> sup)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        final ResourceLocation key = new ResourceLocation(modid, name);
        LazyRegistryObject<I> lazyRet = new LazyRegistryObject<>(()->RegistryObject.of(key, this.getType()));
        if (lazyEntries.putIfAbsent((LazyRegistryObject<T>)lazyRet, () -> sup.get().setRegistryName(key)) != null)
        {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }
        return lazyRet;
    }

    /**
     * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
     * See the example usage.
     *
     * @param bus The Mod Specific event bus.
     */
    public void register(IEventBus bus)
    {
        bus.addListener(this::addEntries);
    }

    /**
     * Stream and collect each time, since can't really cache in the same {@link #entriesView} does
     * This should not be a costly operation however, since the {@link RegistryObject} is cached.
     * The modders can also cache the entries themselves.
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries()
    {
        if(entriesView.isEmpty())
        {
            return Collections.unmodifiableSet(lazyEntries.keySet().stream().map(LazyRegistryObject::getRegistryObject).collect(Collectors.toSet()));
        }
        return entriesView;
    }

    private void addEntries(RegistryEvent.Register<?> event)
    {
        if (event.getGenericType() == this.getType().getRegistrySuperType())
        {
            @SuppressWarnings("unchecked")
            IForgeRegistry<T> reg = (IForgeRegistry<T>)event.getRegistry();
            if(!entries.isEmpty())
            {
                for (Entry<RegistryObject<T>, Supplier<? extends T>> e : entries.entrySet())
                {
                    reg.register(e.getValue().get());
                    e.getKey().updateReference(reg);
                }
            }
            else
            {
                for (Entry<LazyRegistryObject<T>, Supplier<? extends T>> e : lazyEntries.entrySet())
                {
                    reg.register(e.getValue().get());
                    e.getKey().getRegistryObject().updateReference(reg);
                }
            }
        }
    }

    /**
     * If the IForgeRegistry is null, gets it from the supplier. This is almost certain to be called from a classic register event.
     * If it is, it is even guaranteed to be called from the Register Block event, since it is the first one called.
     * The only way it is not called from the event, is by calling {@link #getEntries()} too early. See {@link #DeferredRegister(NonNullSupplier, String)}
     *
     * This also makes sure that the IForgeRegistry is correct when updating the RegistryObjects.
     *
     * Only useful for custom registries, when the {@link IForgeRegistry} does not exist when static init happens
     */
    private IForgeRegistry<T> getType()
    {
        if(type == null)
        {
            type = typeSup.get();
        }
        return type;
    }

    /**
     * A way of lazily evaluating registry objects but in slightly nicer way than using {@link NonNullLazy<RegistryObject>} directly
     *
     * Also a nonnull supplier, to mimic {@link RegistryObject} as best as possible
     */
    public static class LazyRegistryObject<T extends IForgeRegistryEntry<? super T>> implements NonNullSupplier<T>
    {
        private final NonNullLazy<RegistryObject<T>> lazyRet;
        private RegistryObject<T> ret;
        private LazyRegistryObject(NonNullSupplier<RegistryObject<T>> supplier)
        {
            Objects.requireNonNull(supplier);
            lazyRet = NonNullLazy.of(supplier);
        }

        /**
         * This is only called in {@link #addEntries} when the IForgeRegistry is definitely assigned.
         * The RegistryObject is created using {@link RegistryObject#of(ResourceLocation, IForgeRegistry)}
         * It is only created once from the passed in supplier, to not evaluate it each time with {@link #get()}
         */
        private RegistryObject<T> getRegistryObject()
        {
            if(ret == null)
            {
                ret = lazyRet.get();
            }
            return ret;
        }

        /**
         * Mostly for convenience, so that there is no need to do get().get(), but also to act as a Supplier just like RegistryObject
         */
        @Nonnull
        @Override
        public T get()
        {
            return getRegistryObject().get();
        }
    }
}
