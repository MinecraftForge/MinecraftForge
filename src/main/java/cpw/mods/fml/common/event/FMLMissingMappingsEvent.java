package cpw.mods.fml.common.event;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.ModContainer;

/**
 * This event is fired if a world is loaded that has block and item mappings referring the mod that are not
 * in existence.
 * These can be remapped to other existing objects, or simply discarded.
 *
 * @author cpw
 *
 */
public class FMLMissingMappingsEvent extends FMLEvent {
    public static enum Type { BLOCK, ITEM }
    public static class MissingMapping {
        public final Type type;
        public final String name;
        private String remapTarget;
        private List<MissingMapping> remaps;
        public MissingMapping(String name, List<MissingMapping> remaps)
        {
            this.type = name.charAt(0) == '\u0001' ? Type.BLOCK : Type.ITEM;
            this.name = name;
            this.remaps = remaps;
        }
        public void remapTo(String target)
        {
            this.remapTarget = target;
            remaps.add(this);
        }

        public String getRemapTarget()
        {
            return this.remapTarget;
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
