package net.minecraftforge.registries;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.GenericEvent;
import net.minecraftforge.fml.ModContainer;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS forge bus}.
 */
public class MissingMappingsEvent<T> extends GenericEvent<T>
{
    private final IForgeRegistry<T> registry;
    private final ResourceLocation name;
    private final ImmutableList<Mapping<T>> mappings;
    private ModContainer activeMod;

    public MissingMappingsEvent(ResourceLocation name, IForgeRegistry<T> registry, Collection<Mapping<T>> missed)
    {
        super(registry.getRegistrySuperType());
        this.registry = registry;
        this.name = name;
        this.mappings = ImmutableList.copyOf(missed);
    }

    public void setModContainer(ModContainer mod)
    {
        this.activeMod = mod;
    }

    public ResourceLocation getName()
    {
        return this.name;
    }

    public IForgeRegistry<T> getRegistry()
    {
        return this.registry;
    }

    /*
     * This used to be fired on the Mod specific bus, and we could tell which mod was asking for mappings.
     * It no longer is, so this method is useless and just returns getAllMappings.
     * TODO: Ask cpw how if he wants to re-enable the ModBus rethrow.
     */
    @Deprecated
    public ImmutableList<Mapping<T>> getMappings()
    {
        return this.activeMod == null ? getAllMappings() : getMappings(this.activeMod.getModId());
    }

    public ImmutableList<Mapping<T>> getMappings(String modid)
    {
        return ImmutableList.copyOf(this.mappings.stream().filter(e -> e.key.getNamespace().equals(modid)).collect(Collectors.toList()));
    }

    public ImmutableList<Mapping<T>> getAllMappings()
    {
        return this.mappings;
    }

    /**
     * Actions you can take with this missing mapping.
     * <ul>
     * <li>{@link #IGNORE} means this missing mapping will be ignored.
     * <li>{@link #WARN} means this missing mapping will generate a warning.
     * <li>{@link #FAIL} means this missing mapping will prevent the world from loading.
     * </ul>
     */
    public enum Action
    {
        /**
         * Take the default action
         */
        DEFAULT,
        /**
         * Ignore this missing mapping. This means the mapping will be abandoned
         */
        IGNORE,
        /**
         * Generate a warning but allow loading to continue
         */
        WARN,
        /**
         * Fail to load
         */
        FAIL,
        /**
         * Remap this name to a new name (add a migration mapping)
         */
        REMAP
    }

    public static class Mapping<T> implements Comparable<Mapping<T>>
    {
        public final IForgeRegistry<T> registry;
        private final IForgeRegistry<T> pool;
        public final ResourceLocation key;
        public final int id;
        private Action action = Action.DEFAULT;
        private T target;

        public Mapping(IForgeRegistry<T> registry, IForgeRegistry<T> pool, ResourceLocation key, int id)
        {
            this.registry = registry;
            this.pool = pool;
            this.key = key;
            this.id = id;
        }

        /**
         * Ignore the missing item.
         */
        public void ignore()
        {
            action = Action.IGNORE;
        }

        /**
         * Warn the user about the missing item.
         */
        public void warn()
        {
            action = Action.WARN;
        }

        /**
         * Prevent the world from loading due to the missing item.
         */
        public void fail()
        {
            action = Action.FAIL;
        }

        /**
         * Remap the missing entry to the specified object.
         * <p>
         * Use this if you have renamed an entry.
         * Existing references using the old name will point to the new one.
         *
         * @param target Entry to remap to.
         */
        public void remap(T target)
        {
            Validate.notNull(target, "Remap target can not be null");
            Validate.isTrue(pool.getKey(target) != null,
                    String.format(Locale.ENGLISH, "The specified entry %s hasn't been registered in registry yet.", target));
            action = Action.REMAP;
            this.target = target;
        }

        // internal
        public Action getAction()
        {
            return this.action;
        }

        public T getTarget()
        {
            return target;
        }

        @Override
        public int compareTo(Mapping<T> o)
        {
            int ret = this.registry.getRegistryName().compareNamespaced(o.registry.getRegistryName());
            if (ret == 0)
                ret = this.key.compareNamespaced(o.key);
            return ret;
        }
    }
}
