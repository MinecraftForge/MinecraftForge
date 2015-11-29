package net.minecraftforge.fml.common.event;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

/**
 * This event is fired if a world is loaded that has block and item mappings referring the mod that are not
 * in existence.
 * These can be remapped to other existing objects, or simply discarded.
 * Use get() and getAll() to process this event.
 *
 * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
 * @author cpw
 * @author Player
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
    public enum Action {
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
        REMAP,
        /**
         * Allow a block to exist without itemblock anymore
         */
        BLOCKONLY
    }
    public static class MissingMapping {
        public final GameRegistry.Type type;
        public final String name;
        public final int id;
        public final ResourceLocation resourceLocation;
        private Action action = Action.DEFAULT;
        private Object target;

        public MissingMapping(GameRegistry.Type type, ResourceLocation name, int id)
        {
            this.type = type;
            this.name = name.toString();
            this.id = id;
            this.resourceLocation = name;
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
         * Remap the missing item to the specified Block.
         *
         * Use this if you have renamed a Block, don't forget to handle the ItemBlock.
         * Existing references using the old name will point to the new one.
         *
         * @param target Block to remap to.
         */
        public void remap(Block target)
        {
            if (type != GameRegistry.Type.BLOCK) throw new IllegalArgumentException("Can't remap an item to a block.");
            if (target == null) throw new NullPointerException("remap target is null");
            if (GameData.getBlockRegistry().getId(target) < 0) throw new IllegalArgumentException(String.format("The specified block %s hasn't been registered at startup.", target));

            action = Action.REMAP;
            this.target = target;
        }

        /**
         * Remap the missing item to the specified Item.
         *
         * Use this if you have renamed an Item.
         * Existing references using the old name will point to the new one.
         *
         * @param target Item to remap to.
         */
        public void remap(Item target)
        {
            if (type != GameRegistry.Type.ITEM) throw new IllegalArgumentException("Can't remap a block to an item.");
            if (target == null) throw new NullPointerException("remap target is null");
            if (GameData.getItemRegistry().getId(target) < 0) throw new IllegalArgumentException(String.format("The specified item %s hasn't been registered at startup.", target));

            action = Action.REMAP;
            this.target = target;
        }

        public void skipItemBlock()
        {
            if (type != GameRegistry.Type.ITEM) throw new IllegalArgumentException("Cannot skip an item that is a block");
            if (GameData.getBlockRegistry().getRaw(id) == null) throw new IllegalArgumentException("Cannot skip an ItemBlock that doesn't have a Block");
            action = Action.BLOCKONLY;
        }
        // internal

        public Action getAction()
        {
            return this.action;
        }

        public Object getTarget()
        {
            return target;
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
     *
     * Process the list entries by calling ignore(), warn(), fail() or remap() on each entry.
     *
     * @return list of missing mappings
     */
    public List<MissingMapping> get()
    {
        return ImmutableList.copyOf(missing.get(activeContainer.getModId()));
    }

    /**
     * Get the list of missing mappings for all mods.
     *
     * Only use this if you need to handle mod id changes, e.g. if you renamed your mod or
     * split/merge into/from multiple mods.
     *
     * Process the list entries by calling ignore(), warn(), fail() or remap() on each entry you
     * want to handle.
     *
     * @return list of missing mappings
     */
    public List<MissingMapping> getAll()
    {
        return ImmutableList.copyOf(missing.values());
    }
}
