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

package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.reflect.TypeToken;

/**
 * Utility class to help with managing registry entries.
 * Maintains a list of all suppliers for entries and registers them during the proper Register event.
 * Suppliers should return NEW instances every time.
 *
 *Example Usage:
 *<pre>
 *   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
 *   private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
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
    /**
     * Use for vanilla/forge registries. See example above.
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(IForgeRegistry<B> reg, String modid)
    {
        return new DeferredRegister<B>(reg, modid);
    }

    /**
     * Use for custom registries that are made during the NewRegistry event.
     */
    public static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(Class<B> base, String modid)
    {
        return new DeferredRegister<B>(base, modid);
    }

    private final Class<T> superType;
    private final String modid;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

    private IForgeRegistry<T> type;
    private Supplier<RegistryBuilder<T>> registryFactory;
    private boolean seenRegisterEvent = false;

    private DeferredRegister(Class<T> base, String modid)
    {
        this.superType = base;
        this.modid = modid;
    }

    private DeferredRegister(IForgeRegistry<T> reg, String modid)
    {
        this(reg.getRegistrySuperType(), modid);
        this.type = reg;
    }

    /**
     * Adds a new supplier to the list of entries to be registered, and returns a RegistryObject that will be populated with the created entry automatically.
     *
     * @param name The new entry's name, it will automatically have the modid prefixed.
     * @param sup A factory for the new entry, it should return a new instance every time it is called.
     * @return A RegistryObject that will be updated with when the entries in the registry change.
     */
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup)
    {
        if (seenRegisterEvent)
            throw new IllegalStateException("Cannot register new entries to DeferredRegister after RegistryEvent.Register has been fired.");
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);
        final ResourceLocation key = new ResourceLocation(modid, name);

        RegistryObject<I> ret;
        if (this.type != null)
            ret = RegistryObject.of(key, this.type);
        else if (this.superType != null)
            ret = RegistryObject.of(key, this.superType, this.modid);
        else
            throw new IllegalStateException("Could not create RegistryObject in DeferredRegister");

        if (entries.putIfAbsent((RegistryObject<T>) ret, () -> sup.get().setRegistryName(key)) != null) {
            throw new IllegalArgumentException("Duplicate registration " + name);
        }

        return ret;
    }

    /**
     * For custom registries only, fills the {@link #registryFactory} to be called later see {@link #register(IEventBus)}
     *
     * Calls {@link RegistryBuilder#setName} and {@link RegistryBuilder#setType} automatically.
     *
     * @param name  Path of the registry's {@link ResourceLocation}
     * @param sup   Supplier of the RegistryBuilder that is called to fill {@link #type} during the NewRegistry event
     * @return      A supplier of the {@link IForgeRegistry} created by the builder.
     */
    public Supplier<IForgeRegistry<T>> makeRegistry(final String name, final Supplier<RegistryBuilder<T>> sup) {
        if (this.superType == null)
            throw new IllegalStateException("Cannot create a registry without specifying a base type");
        if (this.type != null || this.registryFactory != null)
            throw new IllegalStateException("Cannot create a registry for a type that already exists");

        this.registryFactory = () -> sup.get().setName(new ResourceLocation(modid, name)).setType(this.superType);
        return () -> this.type;
    }

    /**
     * Adds our event handler to the specified event bus, this MUST be called in order for this class to function.
     * See the example usage.
     *
     * @param bus The Mod Specific event bus.
     */
    public void register(IEventBus bus)
    {
        bus.register(new EventDispatcher(this));
        if (this.type == null && this.registryFactory != null) {
            bus.addListener(this::createRegistry);
        }
    }
    public static class EventDispatcher {
        private final DeferredRegister<?> register;

        public EventDispatcher(final DeferredRegister<?> register) {
            this.register = register;
        }

        @SubscribeEvent
        public void handleEvent(RegistryEvent.Register<?> event) {
            register.addEntries(event);
        }
    }
    /**
     * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
     */
    public Collection<RegistryObject<T>> getEntries()
    {
        return entriesView;
    }

    private void addEntries(RegistryEvent.Register<?> event)
    {
        if (this.type == null && this.registryFactory == null)
        {
            //If there is no type yet and we don't have a registry factory, attempt to capture the registry
            //Note: This will only ever get run on the first registry event, as after that time,
            // the type will no longer be null. This is needed here rather than during the NewRegistry event
            // to ensure that mods can properly use deferred registers for custom registries added by other mods
            captureRegistry();
        }
        if (this.type != null && event.getGenericType() == this.type.getRegistrySuperType())
        {
            this.seenRegisterEvent = true;
            @SuppressWarnings("unchecked")
            IForgeRegistry<T> reg = (IForgeRegistry<T>)event.getRegistry();
            for (Entry<RegistryObject<T>, Supplier<? extends T>> e : entries.entrySet())
            {
                reg.register(e.getValue().get());
                e.getKey().updateReference(reg);
            }
        }
    }

    private void createRegistry(RegistryEvent.NewRegistry event)
    {
        this.type = this.registryFactory.get().create();
    }

    private void captureRegistry()
    {
        if (this.superType != null)
        {
            this.type = RegistryManager.ACTIVE.getRegistry(this.superType);
            if (this.type == null)
                throw new IllegalStateException("Unable to find registry for type " + this.superType.getName() + " for modid \"" + modid + "\" after NewRegistry event");
        }
        else
            throw new IllegalStateException("Unable to find registry for mod \"" + modid + "\" No lookup criteria specified.");
    }
}
