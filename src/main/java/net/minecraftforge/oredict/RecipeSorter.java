package net.minecraftforge.oredict;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import net.minecraftforge.fml.common.toposort.TopologicalSort.DirectedGraph;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBookCloning;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.item.crafting.RecipeRepairItem;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.item.crafting.RecipesMapCloning;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.item.crafting.RecipesBanners.RecipeAddPattern;
import net.minecraft.item.crafting.RecipesBanners.RecipeDuplicatePattern;
import static net.minecraftforge.oredict.RecipeSorter.Category.*;

@SuppressWarnings("rawtypes")
public class RecipeSorter implements Comparator<IRecipe>
{
    public enum Category
    {
        UNKNOWN,
        SHAPELESS,
        SHAPED
    };

    private static class SortEntry
    {
        private String name;
        private Class<?> cls;
        private Category cat;
        List<String> before = Lists.newArrayList();
        List<String> after = Lists.newArrayList();

        private SortEntry(String name, Class<?> cls, Category cat, String deps)
        {
            this.name = name;
            this.cls = cls;
            this.cat = cat;
            parseDepends(deps);
        }

        private void parseDepends(String deps)
        {
            if (deps.isEmpty()) return;
            for (String dep : deps.split(" "))
            {
                if (dep.startsWith("before:"))
                {
                    before.add(dep.substring(7));
                }
                else if (dep.startsWith("after:"))
                {
                    after.add(dep.substring(6));
                }
                else
                {
                    throw new IllegalArgumentException("Invalid dependancy: " + dep);
                }
            }
        }

        @Override
        public String toString()
        {
            StringBuilder buf = new StringBuilder();
            buf.append("RecipeEntry(\"").append(name).append("\", ");
            buf.append(cat.name()).append(", ");
            buf.append(cls ==  null ? "" : cls.getName()).append(")");

            if (before.size() > 0)
            {
                buf.append(" Before: ").append(Joiner.on(", ").join(before));
            }

            if (after.size() > 0)
            {
                buf.append(" After: ").append(Joiner.on(", ").join(after));
            }

            return buf.toString();
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }
    };

    private static Map<Class, Category>     categories = Maps.newHashMap();
    //private static Map<String, Class>       types = Maps.newHashMap();
    private static Map<String, SortEntry>   entries = Maps.newHashMap();
    private static Map<Class, Integer>      priorities = Maps.newHashMap();

    public static RecipeSorter INSTANCE = new RecipeSorter();
    private static boolean isDirty = true;

    private static SortEntry before = new SortEntry("Before", null, UNKNOWN, "");
    private static SortEntry after  = new SortEntry("After",  null, UNKNOWN, "");

    private RecipeSorter()
    {
        register("minecraft:shaped",       ShapedRecipes.class,       SHAPED,    "before:minecraft:shapeless");
        register("minecraft:mapextending", RecipesMapExtending.class, SHAPED,    "after:minecraft:shaped before:minecraft:shapeless");
        register("minecraft:shapeless",    ShapelessRecipes.class,    SHAPELESS, "after:minecraft:shaped");
        register("minecraft:repair",       RecipeRepairItem.class,    SHAPELESS, "after:minecraft:shapeless"); //Size 4
        register("minecraft:bookcloning",  RecipeBookCloning.class,   SHAPELESS, "after:minecraft:shapeless"); //Size 9
        register("minecraft:fireworks",    RecipeFireworks.class,     SHAPELESS, "after:minecraft:shapeless"); //Size 10
        register("minecraft:armordyes",    RecipesArmorDyes.class,    SHAPELESS, "after:minecraft:shapeless"); //Size 10
        register("minecraft:mapcloning",   RecipesMapCloning.class,   SHAPELESS, "after:minecraft:shapeless"); //Size 10
        register("minecraft:pattern_dupe", RecipeDuplicatePattern.class, SHAPELESS, "after:minecraft:shapeless"); //Size 2
        register("minecraft:pattern_add",  RecipeAddPattern.class,       SHAPELESS, "after:minecraft:shapeless"); //Size 10

        register("forge:shapedore",     ShapedOreRecipe.class,    SHAPED,    "after:minecraft:shaped before:minecraft:shapeless");
        register("forge:shapelessore",  ShapelessOreRecipe.class, SHAPELESS, "after:minecraft:shapeless");
    }

    @Override
    public int compare(IRecipe r1, IRecipe r2)
    {
        Category c1 = getCategory(r1);
        Category c2 = getCategory(r2);
        if (c1 == SHAPELESS && c2 == SHAPED) return  1;
        if (c1 == SHAPED && c2 == SHAPELESS) return -1;
        if (r2.getRecipeSize() < r1.getRecipeSize()) return -1;
        if (r2.getRecipeSize() > r1.getRecipeSize()) return  1;
        return getPriority(r2) - getPriority(r1); // high priority value first!
    }

    private static Set<Class> warned = Sets.newHashSet();
    public static void sortCraftManager()
    {
        bake();
        FMLLog.fine("Sorting recipies");
        warned.clear();
        Collections.sort(CraftingManager.getInstance().getRecipeList(), INSTANCE);
    }

    public static void register(String name, Class<?> recipe, Category category, String dependancies)
    {
        assert(category != UNKNOWN) : "Category must not be unknown!";
        isDirty = true;

        SortEntry entry = new SortEntry(name, recipe, category, dependancies);
        entries.put(name, entry);
        setCategory(recipe, category);
    }

    public static void setCategory(Class<?> recipe, Category category)
    {
        assert(category != UNKNOWN) : "Category must not be unknown!";
        categories.put(recipe, category);
    }

    public static Category getCategory(IRecipe recipe)
    {
        return getCategory(recipe.getClass());
    }

    public static Category getCategory(Class<?> recipe)
    {
        Class<?> cls = recipe;
        Category ret = categories.get(cls);

        if (ret == null)
        {
            while (cls != Object.class)
            {
                cls = cls.getSuperclass();
                ret = categories.get(cls);
                if (ret != null)
                {
                    categories.put(recipe, ret);
                    return ret;
                }
            }
        }

        return ret == null ? UNKNOWN : ret;
    }

    private static int getPriority(IRecipe recipe)
    {
        Class<?> cls = recipe.getClass();
        Integer ret = priorities.get(cls);

        if (ret == null)
        {
            if (!warned.contains(cls))
            {
                FMLLog.info("  Unknown recipe class! %s Modder please refer to %s", cls.getName(), RecipeSorter.class.getName());
                warned.add(cls);
            }
            cls = cls.getSuperclass();
            while (cls != Object.class)
            {
                ret = priorities.get(cls);
                if (ret != null)
                {
                    priorities.put(recipe.getClass(), ret);
                    FMLLog.fine("    Parent Found: %d - %s", ret.intValue(), cls.getName());
                    return ret.intValue();
                }
            }
        }

        return ret == null ? 0 : ret.intValue();
    }

    private static void bake()
    {
        if (!isDirty) return;
        FMLLog.fine("Forge RecipeSorter Baking:");
        DirectedGraph<SortEntry> sorter = new DirectedGraph<SortEntry>();
        sorter.addNode(before);
        sorter.addNode(after);
        sorter.addEdge(before, after);

        for (Map.Entry<String, SortEntry> entry : entries.entrySet())
        {
            sorter.addNode(entry.getValue());
        }

        for (Map.Entry<String, SortEntry> e : entries.entrySet())
        {
            SortEntry entry = e.getValue();
            boolean postAdded = false;

            sorter.addEdge(before, entry);
            for (String dep : entry.after)
            {
                if (entries.containsKey(dep))
                {
                    sorter.addEdge(entries.get(dep), entry);
                }
            }

            for (String dep : entry.before)
            {
                postAdded = true;
                sorter.addEdge(entry, after);
                if (entries.containsKey(dep))
                {
                    sorter.addEdge(entry, entries.get(dep));
                }
            }

            if (!postAdded)
            {
                sorter.addEdge(entry, after);
            }
        }


        List<SortEntry> sorted = TopologicalSort.topologicalSort(sorter);
        int x = sorted.size();
        for (SortEntry entry : sorted)
        {
            FMLLog.fine("  %d: %s", x, entry);
            priorities.put(entry.cls, x--);
        }
    }
}
