package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.Event;

public class OreDictionary
{
    private static boolean hasInit = false;
    private static List<String>          idToName = new ArrayList<String>();
    private static Map<String, Integer>  nameToId = new HashMap<String, Integer>();
    private static List<ArrayList<ItemStack>> idToStack = Lists.newArrayList(); //ToDo: Unqualify to List when possible {1.8}
    private static List<ArrayList<ItemStack>> idToStackUn = Lists.newArrayList(); //ToDo: Unqualify to List when possible {1.8}
    private static Map<Integer, List<Integer>> stackToId = Maps.newHashMap();
    public static final ArrayList<ItemStack> EMPTY_LIST = new UnmodifiableArrayList(Lists.newArrayList()); //ToDo: Unqualify to List when possible {1.8}

    /**
     * Minecraft changed from -1 to Short.MAX_VALUE in 1.5 release for the "block wildcard". Use this in case it
     * changes again.
     */
    public static final int WILDCARD_VALUE = Short.MAX_VALUE;

    static {
        initVanillaEntries();
    }

    @SuppressWarnings("unchecked")
    public static void initVanillaEntries()
    {
        if (!hasInit)
        {
            registerOre("logWood",     new ItemStack(Blocks.log, 1, WILDCARD_VALUE));
            registerOre("logWood",     new ItemStack(Blocks.log2, 1, WILDCARD_VALUE));
            registerOre("plankWood",   new ItemStack(Blocks.planks, 1, WILDCARD_VALUE));
            registerOre("slabWood",    new ItemStack(Blocks.wooden_slab, 1, WILDCARD_VALUE));
            registerOre("stairWood",   Blocks.oak_stairs);
            registerOre("stairWood",   Blocks.spruce_stairs);
            registerOre("stairWood",   Blocks.birch_stairs);
            registerOre("stairWood",   Blocks.jungle_stairs);
            registerOre("stairWood",   Blocks.acacia_stairs);
            registerOre("stairWood",   Blocks.dark_oak_stairs);
            registerOre("stickWood",   Items.stick);
            registerOre("treeSapling", new ItemStack(Blocks.sapling, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.leaves, 1, WILDCARD_VALUE));
            registerOre("treeLeaves",  new ItemStack(Blocks.leaves2, 1, WILDCARD_VALUE));
            registerOre("oreGold",     Blocks.gold_ore);
            registerOre("oreIron",     Blocks.iron_ore);
            registerOre("oreLapis",    Blocks.lapis_ore);
            registerOre("oreDiamond",  Blocks.diamond_ore);
            registerOre("oreRedstone", Blocks.redstone_ore);
            registerOre("oreEmerald",  Blocks.emerald_ore);
            registerOre("oreQuartz",   Blocks.quartz_ore);
            registerOre("oreCoal",     Blocks.coal_ore);
            registerOre("blockGold",     Blocks.gold_block);
            registerOre("blockIron",     Blocks.iron_block);
            registerOre("blockLapis",    Blocks.lapis_block);
            registerOre("blockDiamond",  Blocks.diamond_block);
            registerOre("blockRedstone", Blocks.redstone_block);
            registerOre("blockEmerald",  Blocks.emerald_block);
            registerOre("blockQuartz",   Blocks.quartz_block);
            registerOre("blockCoal",     Blocks.coal_block);
            registerOre("blockGlassColorless", Blocks.glass);
            registerOre("blockGlass",    Blocks.glass);
            registerOre("blockGlass",    new ItemStack(Blocks.stained_glass, 1, WILDCARD_VALUE));
            //blockGlass{Color} is added below with dyes
            registerOre("paneGlassColorless", Blocks.glass_pane);
            registerOre("paneGlass",     Blocks.glass_pane);
            registerOre("paneGlass",     new ItemStack(Blocks.stained_glass_pane, 1, WILDCARD_VALUE));
            //paneGlass{Color} is added below with dyes
            registerOre("ingotIron",     Items.iron_ingot);
            registerOre("ingotGold",     Items.gold_ingot);
            registerOre("ingotBrick",    Items.brick);
            registerOre("ingotBrickNether", Items.netherbrick);
            registerOre("nuggetGold",  Items.gold_nugget);
            registerOre("gemDiamond",  Items.diamond);
            registerOre("gemEmerald",  Items.emerald);
            registerOre("gemQuartz",   Items.quartz);
            registerOre("dustRedstone",  Items.redstone);
            registerOre("dustGlowstone", Items.glowstone_dust);
            registerOre("gemLapis",    new ItemStack(Items.dye, 1, 4));
            registerOre("slimeball",   Items.slime_ball);
            registerOre("glowstone",   Blocks.glowstone);
            registerOre("cropWheat",   Items.wheat);
            registerOre("cropPotato",  Items.potato);
            registerOre("cropCarrot",  Items.carrot);
            registerOre("stone",       Blocks.stone);
            registerOre("cobblestone", Blocks.cobblestone);
            registerOre("sandstone",   new ItemStack(Blocks.sandstone, 1, WILDCARD_VALUE));
            registerOre("dye",         new ItemStack(Items.dye, 1, WILDCARD_VALUE));
            registerOre("record",      Items.record_13);
            registerOre("record",      Items.record_cat);
            registerOre("record",      Items.record_blocks);
            registerOre("record",      Items.record_chirp);
            registerOre("record",      Items.record_far);
            registerOre("record",      Items.record_mall);
            registerOre("record",      Items.record_mellohi);
            registerOre("record",      Items.record_stal);
            registerOre("record",      Items.record_strad);
            registerOre("record",      Items.record_ward);
            registerOre("record",      Items.record_11);
            registerOre("record",      Items.record_wait);
        }

        // Build our list of items to replace with ore tags
        Map<ItemStack, String> replacements = new HashMap<ItemStack, String>();
        replacements.put(new ItemStack(Items.stick), "stickWood");
        replacements.put(new ItemStack(Blocks.planks), "plankWood");
        replacements.put(new ItemStack(Blocks.planks, 1, WILDCARD_VALUE), "plankWood");
        replacements.put(new ItemStack(Blocks.stone), "stone");
        replacements.put(new ItemStack(Blocks.stone, 1, WILDCARD_VALUE), "stone");
        replacements.put(new ItemStack(Blocks.cobblestone), "cobblestone");
        replacements.put(new ItemStack(Blocks.cobblestone, 1, WILDCARD_VALUE), "cobblestone");
        replacements.put(new ItemStack(Items.gold_ingot), "ingotGold");
        replacements.put(new ItemStack(Items.iron_ingot), "ingotIron");
        replacements.put(new ItemStack(Items.diamond), "gemDiamond");
        replacements.put(new ItemStack(Items.emerald), "gemEmerald");
        replacements.put(new ItemStack(Items.redstone), "dustRedstone");
        replacements.put(new ItemStack(Items.glowstone_dust), "dustGlowstone");
        replacements.put(new ItemStack(Blocks.glowstone), "glowstone");
        replacements.put(new ItemStack(Items.slime_ball), "slimeball");
        replacements.put(new ItemStack(Blocks.glass), "blockGlassColorless");

        // Register dyes
        String[] dyes =
        {
            "Black",
            "Red",
            "Green",
            "Brown",
            "Blue",
            "Purple",
            "Cyan",
            "LightGray",
            "Gray",
            "Pink",
            "Lime",
            "Yellow",
            "LightBlue",
            "Magenta",
            "Orange",
            "White"
        };

        for(int i = 0; i < 16; i++)
        {
            ItemStack dye = new ItemStack(Items.dye, 1, i);
            ItemStack block = new ItemStack(Blocks.stained_glass, 1, 15 - i);
            ItemStack pane = new ItemStack(Blocks.stained_glass_pane, 1, 15 - i);
            if (!hasInit)
            {
                registerOre("dye" + dyes[i], dye);
                registerOre("blockGlass" + dyes[i], block);
                registerOre("paneGlass"  + dyes[i], pane);
            }
            replacements.put(dye,   "dye" + dyes[i]);
            replacements.put(block, "blockGlass" + dyes[i]);
            replacements.put(pane,  "paneGlass" + dyes[i]);
        }
        hasInit = true;

        ItemStack[] replaceStacks = replacements.keySet().toArray(new ItemStack[replacements.keySet().size()]);

        // Ignore recipes for the following items
        ItemStack[] exclusions = new ItemStack[]
        {
            new ItemStack(Blocks.lapis_block),
            new ItemStack(Items.cookie),
            new ItemStack(Blocks.stonebrick),
            new ItemStack(Blocks.stone_slab, 1, WILDCARD_VALUE),
            new ItemStack(Blocks.stone_stairs),
            new ItemStack(Blocks.cobblestone_wall),
            new ItemStack(Blocks.oak_stairs),
            new ItemStack(Blocks.spruce_stairs),
            new ItemStack(Blocks.birch_stairs),
            new ItemStack(Blocks.jungle_stairs),
            new ItemStack(Blocks.acacia_stairs),
            new ItemStack(Blocks.dark_oak_stairs),
            new ItemStack(Blocks.glass_pane),
            new ItemStack(Blocks.stained_glass)
        };

        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        List<IRecipe> recipesToRemove = new ArrayList<IRecipe>();
        List<IRecipe> recipesToAdd = new ArrayList<IRecipe>();

        // Search vanilla recipes for recipes to replace
        for(Object obj : recipes)
        {
            if(obj instanceof ShapedRecipes)
            {
                ShapedRecipes recipe = (ShapedRecipes)obj;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && containsMatch(false, exclusions, output))
                {
                    continue;
                }

                if(containsMatch(true, recipe.recipeItems, replaceStacks))
                {
                    recipesToRemove.add(recipe);
                    recipesToAdd.add(new ShapedOreRecipe(recipe, replacements));
                }
            }
            else if(obj instanceof ShapelessRecipes)
            {
                ShapelessRecipes recipe = (ShapelessRecipes)obj;
                ItemStack output = recipe.getRecipeOutput();
                if (output != null && containsMatch(false, exclusions, output))
                {
                    continue;
                }

                if(containsMatch(true, (ItemStack[])recipe.recipeItems.toArray(new ItemStack[recipe.recipeItems.size()]), replaceStacks))
                {
                    recipesToRemove.add((IRecipe)obj);
                    IRecipe newRecipe = new ShapelessOreRecipe(recipe, replacements);
                    recipesToAdd.add(newRecipe);
                }
            }
        }

        recipes.removeAll(recipesToRemove);
        recipes.addAll(recipesToAdd);
        if (recipesToRemove.size() > 0)
        {
            FMLLog.info("Replaced %d ore recipies", recipesToRemove.size());
        }
    }

    /**
     * Gets the integer ID for the specified ore name.
     * If the name does not have a ID it assigns it a new one.
     *
     * @param name The unique name for this ore 'oreIron', 'ingotIron', etc..
     * @return A number representing the ID for this ore type
     */
    public static int getOreID(String name)
    {
        Integer val = nameToId.get(name);
        if (val == null)
        {
            idToName.add(name);
            val = idToName.size() - 1; //0 indexed
            nameToId.put(name, val);
            idToStack.add(new ArrayList<ItemStack>());
            idToStackUn.add(new UnmodifiableArrayList(idToStack.get(val)));
        }
        return val;
    }

    /**
     * Reverse of getOreID, will not create new entries.
     *
     * @param id The ID to translate to a string
     * @return The String name, or "Unknown" if not found.
     */
    public static String getOreName(int id)
    {
        return (id >= 0 && id < idToName.size()) ? idToName.get(id) : "Unknown";
    }

    /**
     * Gets the integer ID for the specified item stack.
     * If the item stack is not linked to any ore, this will return -1 and no new entry will be created.
     *
     * @param stack The item stack of the ore.
     * @return A number representing the ID for this ore type, or -1 if couldn't find it.
     */
    @Deprecated // Use getOreIds below for more accuracy
    public static int getOreID(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return -1;

        int id = Item.getIdFromItem(stack.getItem());
        List<Integer> ids = stackToId.get(id); //Try the wildcard first
        if (ids == null || ids.size() == 0)
        {
            ids = stackToId.get(id | ((stack.getItemDamage() + 1) << 16)); // Mow the Meta specific one, +1 so that meta 0 is significant
        }
        return (ids != null && ids.size() > 0) ? ids.get(0) : -1;
    }

    /**
     * Gets all the integer ID for the ores that the specified item stakc is registered to.
     * If the item stack is not linked to any ore, this will return an empty array and no new entry will be created.
     *
     * @param stack The item stack of the ore.
     * @return An array of ids that this ore is registerd as.
     */
    public static int[] getOreIDs(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return new int[0];

        Set<Integer> set = new HashSet<Integer>();

        int id = Item.getIdFromItem(stack.getItem());
        List<Integer> ids = stackToId.get(id);
        if (ids != null) set.addAll(ids);
        ids = stackToId.get(id | ((stack.getItemDamage() + 1) << 16));
        if (ids != null) set.addAll(ids);

        Integer[] tmp = set.toArray(new Integer[set.size()]);
        int[] ret = new int[tmp.length];
        for (int x = 0; x < tmp.length; x++)
            ret[x] = tmp[x];
        return ret;
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * The returned List is unmodifiable, but will be updated if a new ore
     * is registered using registerOre
     *
     * @param name The ore name, directly calls getOreID
     * @return An arrayList containing ItemStacks registered for this ore
     */
    public static ArrayList<ItemStack> getOres(String name) //TODO: 1.8 ArrayList -> List
    {
        return getOres(getOreID(name));
    }

    /**
     * Retrieves a list of all unique ore names that are already registered.
     *
     * @return All unique ore names that are currently registered.
     */
    public static String[] getOreNames()
    {
        return idToName.toArray(new String[idToName.size()]);
    }

    /**
     * Retrieves the ArrayList of items that are registered to this ore type.
     * Creates the list as empty if it did not exist.
     *
     * Warning: In 1.8, the return value will become a immutible list,
     * and this function WILL NOT create the entry if the ID doesn't exist,
     * IDs are intended to be internal OreDictionary things and modders
     * should not ever code them in.
     *
     * @param id The ore ID, see getOreID
     * @return An List containing ItemStacks registered for this ore
     */
    @Deprecated // Use the named version not int
    public static ArrayList<ItemStack> getOres(Integer id) //TODO: delete in 1.8 in favor of unboxed version below
    {
       return getOres((int)id.intValue());
    }
    private static ArrayList<ItemStack> getOres(int id) //TODO: change to ImmutibleList<ItemStack> in 1.8, also make private
    {
        while (idToName.size() < id + 1) // TODO: Remove this in 1.8, this is only for backwards compatibility
        {
            String name = "Filler: " + idToName.size();
            idToName.add(name);
            nameToId.put(name, idToName.size() - 1); //0 indexed
            idToStack.add(null);
            idToStackUn.add(EMPTY_LIST);
        }
        return idToStackUn.size() > id ? idToStackUn.get(id) : EMPTY_LIST;
    }

    private static boolean containsMatch(boolean strict, ItemStack[] inputs, ItemStack... targets)
    {
        for (ItemStack input : inputs)
        {
            for (ItemStack target : targets)
            {
                if (itemMatches(target, input, strict))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsMatch(boolean strict, List<ItemStack> inputs, ItemStack... targets)
    {
        for (ItemStack input : inputs)
        {
            for (ItemStack target : targets)
            {
                if (itemMatches(target, input, strict))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean itemMatches(ItemStack target, ItemStack input, boolean strict)
    {
        if (input == null && target != null || input != null && target == null)
        {
            return false;
        }
        return (target.getItem() == input.getItem() && ((target.getItemDamage() == WILDCARD_VALUE && !strict) || target.getItemDamage() == input.getItemDamage()));
    }

    //Convenience functions that make for cleaner code mod side. They all drill down to registerOre(String, int, ItemStack)
    public static void registerOre(String name, Item      ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(String name, Block     ore){ registerOre(name, new ItemStack(ore));  }
    public static void registerOre(String name, ItemStack ore){ registerOreImpl(name, ore);             }
    @Deprecated //Use named, not ID in 1.8+
    public static void registerOre(int    id,   Item      ore){ registerOre(id,   new ItemStack(ore));  }
    @Deprecated //Use named, not ID in 1.8+
    public static void registerOre(int    id,   Block     ore){ registerOre(id,   new ItemStack(ore));  }
    @Deprecated //Use named, not ID in 1.8+
    public static void registerOre(int    id,   ItemStack ore){ registerOreImpl(getOreName(id), ore);   }

    /**
     * Registers a ore item into the dictionary.
     * Raises the registerOre function in all registered handlers.
     *
     * @param name The name of the ore
     * @param id The ID of the ore
     * @param ore The ore's ItemStack
     */
    private static void registerOreImpl(String name, ItemStack ore)
    {
        if ("Unknown".equals(name)) return; //prevent bad IDs.

        int oreID = getOreID(name);
        int hash = Item.getIdFromItem(ore.getItem());
        if (ore.getItemDamage() != WILDCARD_VALUE)
        {
            hash |= ((ore.getItemDamage() + 1) << 16); // +1 so 0 is significant
        }

        //Add things to the baked version, and prevent duplicates
        List<Integer> ids = stackToId.get(hash);
        if (ids != null && ids.contains(oreID)) return;
        if (ids == null)
        {
            ids = Lists.newArrayList();
            stackToId.put(hash, ids);
        }
        ids.add(oreID);

        //Add to the unbaked version
        ore = ore.copy();
        idToStack.get(oreID).add(ore);
        MinecraftForge.EVENT_BUS.post(new OreRegisterEvent(name, ore));
    }

    public static class OreRegisterEvent extends Event
    {
        public final String Name;
        public final ItemStack Ore;

        public OreRegisterEvent(String name, ItemStack ore)
        {
            this.Name = name;
            this.Ore = ore;
        }
    }

    public static void rebakeMap()
    {
        //System.out.println("Baking OreDictionary:");
        stackToId.clear();
        for (int id = 0; id < idToStack.size(); id++)
        {
            List<ItemStack> ores = idToStack.get(id);
            if (ores == null) continue;
            for (ItemStack ore : ores)
            {
                int hash = Item.getIdFromItem(ore.getItem());
                if (ore.getItemDamage() != WILDCARD_VALUE)
                {
                    hash |= ((ore.getItemDamage() + 1) << 16); // +1 so meta 0 is significant
                }
                List<Integer> ids = stackToId.get(hash);
                if (ids == null)
                {
                    ids = Lists.newArrayList();
                    stackToId.put(hash, ids);
                }
                ids.add(id);
                //System.out.println(id + " " + getOreName(id) + " " + Integer.toHexString(hash) + " " + ore);
            }
        }
    }


    //Pulled from Collections.UnmodifiableList, as we need to explicitly subclass ArrayList for backward compatibility.
    //Delete this class in 1.8 when we loose the ArrayList specific return types.
    private static class UnmodifiableArrayList<E> extends ArrayList<E>
    {
        final ArrayList<? extends E> list;

        UnmodifiableArrayList(ArrayList<? extends E> list)
        {
            super(0);
            this.list = list;
        }

        public ListIterator<E> listIterator() {return listIterator(0);      }
        public boolean  equals(Object o)      { return o == this || list.equals(o); }
        public int      hashCode()            { return list.hashCode();     }
        public E        get(int index)        { return list.get(index);     }
        public int      indexOf(Object o)     { return list.indexOf(o);     }
        public int      lastIndexOf(Object o) { return list.lastIndexOf(o); }
        public int      size()                { return list.size();         }
        public boolean  isEmpty()             { return list.isEmpty();      }
        public boolean  contains(Object o)    { return list.contains(o);    }
        public Object[] toArray()             { return list.toArray();      }
        public <T> T[]  toArray(T[] a)        { return list.toArray(a);     }
        public String   toString()            { return list.toString();     }
        public boolean containsAll(Collection<?> coll) { return list.containsAll(coll); }

        public E set(int index, E element)    { throw new UnsupportedOperationException(); }
        public void add(int index, E element) { throw new UnsupportedOperationException(); }
        public E remove(int index)            { throw new UnsupportedOperationException(); }
        public boolean add(E e)               { throw new UnsupportedOperationException(); }
        public boolean remove(Object o)       { throw new UnsupportedOperationException(); }
        public void clear()                   { throw new UnsupportedOperationException(); }
        public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
        public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
        public boolean addAll(Collection<? extends E> coll) { throw new UnsupportedOperationException(); }
        public boolean addAll(int index, Collection<? extends E> c) { throw new UnsupportedOperationException(); }

        public ListIterator<E> listIterator(final int index)
        {
            return new ListIterator<E>()
            {
                private final ListIterator<? extends E> i = list.listIterator(index);
                public boolean hasNext()     {return i.hasNext();}
                public E next()              {return i.next();}
                public boolean hasPrevious() {return i.hasPrevious();}
                public E previous()          {return i.previous();}
                public int nextIndex()       {return i.nextIndex();}
                public int previousIndex()   {return i.previousIndex();}

                public void remove() { throw new UnsupportedOperationException(); }
                public void set(E e) { throw new UnsupportedOperationException(); }
                public void add(E e) { throw new UnsupportedOperationException(); }
            };
        }

        public List<E> subList(int fromIndex, int toIndex)
        {
            return  Collections.unmodifiableList(list.subList(fromIndex, toIndex));
        }

        public Iterator<E> iterator()
        {
            return new Iterator<E>()
            {
                private final Iterator<? extends E> i = list.iterator();

                public boolean hasNext() { return i.hasNext(); }
                public E next()          { return i.next();    }
                public void remove()     { throw new UnsupportedOperationException(); }
            };
        }
    }
}
