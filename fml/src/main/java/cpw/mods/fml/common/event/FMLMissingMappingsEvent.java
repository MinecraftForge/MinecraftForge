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
    public static enum Action { IGNORE, WARN, FAIL }
    public static class MissingMapping {
        public final GameRegistry.Type type;
        public final String name;
        private Action action;
        private List<MissingMapping> remaps;
        public MissingMapping(String name, List<MissingMapping> remaps)
        {
            this.type = name.charAt(0) == '\u0001' ? GameRegistry.Type.BLOCK : GameRegistry.Type.ITEM;
            this.name = name;
            this.remaps = remaps;
            this.action = FMLCommonHandler.instance().getDefaultMissingAction();
        }
        public void setAction(Action target)
        {
            this.action = target;
            remaps.add(this);
        }

        public Action getAction()
        {
            return this.action;
        }
    }
    private ListMultimap<String,MissingMapping> missing;
    private ModContainer activeContainer;
    private List<MissingMapping> currentList;

    public FMLMissingMappingsEvent(ListMultimap<String,MissingMapping> missingMappings)
    {
        this.missing = missingMappings;
    }
    @Override
    public void applyModContainer(ModContainer activeContainer)
    {
        super.applyModContainer(activeContainer);
        this.activeContainer = activeContainer;
        this.currentList = null;
    }

    /**
     * Get the list of missing mappings for the active mod.
     * @return
     */
    public List<MissingMapping> get()
    {
        if (currentList == null)
        {
            currentList = ImmutableList.copyOf(missing.removeAll(activeContainer.getModId()));
        }
        return currentList;
    }
}
