package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class DeferredCustomRegistry<T extends IForgeRegistryEntry<T>>
{
   private IForgeRegistry<T> type;
   private final NonNullSupplier<IForgeRegistry<T>> typeSup;
   private final String modid;
   private final Map<LazyRegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
   private final Set<LazyRegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

   /**
    * Custom registries are only created after the {@link DeferredRegister} constructor is called due to static init.
    * To solve this, the {@link IForgeRegistry} and {@link RegistryObject}s must be evaluated only when registry events are fired.
    * The passed in supplier will only be called once {@link #addEntries} is invoked.
    * When it is invoked, it is guaranteed that the registry is created since the {@link RegistryEvent.NewRegistry} event is fired beforehand.
    * @param supReg supplier of an IForgeRegistry of the desired type.
    */
   public DeferredCustomRegistry(NonNullSupplier<IForgeRegistry<T>> supReg, String modid)
   {
      this.typeSup = supReg;
      this.modid = modid;
   }

   /**
    * Adds a new supplier to the list of entries to be registered, and returns a {@link LazyRegistryObject}
    * that will be populated with the created entry automatically and lazily.
    *
    * @param name The new entry's name, it will automatically have the modid prefixed.
    * @param sup A factory for the new entry, it should return a new instance every time it is called.
    * @return A {@link LazyRegistryObject} that will be lazily updated with when the entries in the registry change.
    */
   public <I extends T> LazyRegistryObject<I> lazyRegister(final String name, final Supplier<? extends I> sup)
   {
      Objects.requireNonNull(name);
      Objects.requireNonNull(sup);
      final ResourceLocation key = new ResourceLocation(modid, name);
      LazyRegistryObject<I> lazyOptRet = new LazyRegistryObject<>(()->RegistryObject.of(key, this.type));
      if(entries.putIfAbsent(lazyOptRet.cast(), ()->sup.get().setRegistryName(key)) != null) {
         throw new IllegalArgumentException("Duplicate registration " + name);
      }
      return lazyOptRet;
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
    * @return The unmodifiable view of registered entries. Useful for bulk operations on all values.
    */
   public Collection<LazyRegistryObject<T>> getEntries()
   {
      return entriesView;
   }

   private void addEntries(RegistryEvent.Register<?> event)
   {
      if (event.getGenericType() == getFromSupplier().getRegistrySuperType())
      {
         @SuppressWarnings("unchecked")
         IForgeRegistry<T> reg = (IForgeRegistry<T>)event.getRegistry();
         for (Map.Entry<LazyRegistryObject<T>, Supplier<? extends T>> e : entries.entrySet())
         {
            reg.register(e.getValue().get());
            e.getKey().getRegistryObject().updateReference(reg);
         }
      }
   }

   /**
    * If the IForgeRegistry is null, gets it from the supplier. This is sure to be called from a classic register event.
    * It is even guaranteed to be called from the Register Block event, since it is the first one called.
    * This also makes sure that the IForgeRegistry is correct when updating the RegistryObjects.
    */
   private IForgeRegistry<T> getFromSupplier()
   {
      if(type == null) {
         type = typeSup.get();
      }
      return type;
   }

   /**
    * A way of lazily evaluating registry objects but in a nicer way than using {@link LazyOptional<RegistryObject>}
    */
   public static class LazyRegistryObject<T extends IForgeRegistryEntry<? super T>> implements Supplier<T>
   {
      private final LazyOptional<RegistryObject<T>> lazyRet;
      private RegistryObject<T> ret;
      private LazyRegistryObject(NonNullSupplier<RegistryObject<T>> supplier)
      {
         if(supplier == null)
         {
            throw new IllegalStateException("Supplier should not be null");
         }
         lazyRet = LazyOptional.of(supplier);
      }

      /**
       * The interest of the LazyOptional is mostly for the lazy part.
       * This is only called in {@link #addEntries} when the IForgeRegistry is definitely assigned.
       * The RegistryObject is created using {@link RegistryObject#of(ResourceLocation, IForgeRegistry)}
       * It is only created once from the passed in supplier, to not evaluate it each time with {@link #get()}
       */
      private RegistryObject<T> getRegistryObject()
      {
         if(ret == null)
         {
            ret = lazyRet.orElseThrow(()->new IllegalStateException("This deserves a better error message."));
         }
         return ret;
      }

      /**
       * Mostly for convenience, so that there is no need to do get().get(), but also to act as a Supplier just like RegistryObject
       */
      @Override
      public T get()
      {
         return getRegistryObject().get();
      }

      /**
       * Helper function to cast, since it is needed in {@link #lazyRegister} to work with
       * objects not directly implementing the type of this registry
       */
      @SuppressWarnings("unchecked")
      private  <X extends IForgeRegistryEntry<? super X>> LazyRegistryObject<X> cast()
      {
         return (LazyRegistryObject<X>) this;
      }
   }
}