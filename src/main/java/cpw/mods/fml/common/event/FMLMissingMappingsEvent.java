package cpw.mods.fml.common.event;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * This event is fired if a world is loaded that has block and item mappings referring the mod that are not
 * in existence.
 * These can be remapped to other existing objects, or simply discarded.
 *
 * @author cpw
 *
 */
public class FMLMissingMappingsEvent extends FMLEvent {
    /**
     * Actions you can take with this missing mapping.
     * <ul>
     * <li>{@link #IGNORE} means this missing mapping will be ignored.
     * <li>{@link #WARN} means this missing mapping will generate a warning.
     * <li>{@link #FAIL} means this missing mapping will prevent the world from loading.
     * </ul>
     * @author cpw
     *
     */
    public static enum Action { DEFAULT, IGNORE, WARN, FAIL }
    public static class MissingMapping {
        public final GameRegistry.Type type;
        public final String name;
        public final int id;
        private Action action = Action.DEFAULT;

        public MissingMapping(String name, int id)
        {
            this.type = name.charAt(0) == '\u0001' ? GameRegistry.Type.BLOCK : GameRegistry.Type.ITEM;
            this.name = name;
            this.id = id;
        }
        /**
         * @deprecated use ignore(), warn() or fail() instead
         */
        @Deprecated
        public void setAction(Action target)
        {
            if (target == Action.DEFAULT) throw new IllegalArgumentException();

            this.action = target;
        }

        public void ignore()
        {
            this.action = Action.IGNORE;
        }

        public void warn()
        {
            this.action = Action.WARN;
        }

        public void fail()
        {
            this.action = Action.FAIL;
        }

        public Action getAction()
        {
            return this.action;
        }
    }
    private ListMultimap<String,MissingMapping> missing;
    private ModContainer activeContainer;

    public FMLMissingMappingsEvent(ListMultimap<String,MissingMapping> missingMappings)
    {
        this.missing = missingMappings;
    }
    @Override
    public void applyModContainer(ModContainer activeContainer)
    {
        super.applyModContainer(activeContainer);
        this.activeContainer = activeContainer;
    }

    /**
     * Get the list of missing mappings for the active mod.
     * @return
     */
    public List<MissingMapping> get()
    {
        return ImmutableList.copyOf(missing.get(activeContainer.getModId()));
    }
}
