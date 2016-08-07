package net.minecraftforge.ingredients;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.RegistryDelegate;
import org.apache.logging.log4j.Level;

import java.util.Map;
import java.util.Set;

public class IngredientRegistry {
    static int maxID = 0;

    static BiMap<String, Ingredient> ingredients = HashBiMap.create();
    static BiMap<Ingredient, Integer> ingredientIDs = HashBiMap.create();
    static BiMap<Integer, String> ingredientNames = HashBiMap.create(); //Caching this just makes some other calls faster
    static BiMap<Block, Ingredient> ingredientBlocks;

    // the globally unique ingredient map - only used to associate non-defaults during world/server loading
    static BiMap<String, Ingredient> masterIngredientReference = HashBiMap.create();
    static BiMap<String, String> defaultIngredientName = HashBiMap.create();
    static Map<Ingredient, IngredientDelegate> delegates = Maps.newHashMap();

    public static final Ingredient IRON     = new Ingredient("iron").setBlock(Blocks.IRON_BLOCK).setDensity(7874).setMeltingPoint(1808).setBoilingPoint(3023);
    public static final Ingredient GOLD     = new Ingredient("gold").setBlock(Blocks.GOLD_BLOCK).setDensity(19300).setMeltingPoint(1337).setBoilingPoint(2973);
    public static final Ingredient DIAMOND  = new Ingredient("diamond").setBlock(Blocks.DIAMOND_BLOCK).setDensity(3539).setMeltingPoint(4600).setBoilingPoint(4600 /**approx sublime point*/).setCanMelt(false);
    public static final Ingredient COAL     = new Ingredient("coal").setBlock(Blocks.COAL_BLOCK).setDensity(833 /*approx bulk density*/).setMeltingPoint(3900).setBoilingPoint(3900 /*approx sublime point*/).setCanMelt(false);
    public static final Ingredient CHARCOAL = new Ingredient("charcoal").setDensity(360/*avg*/);
    public static final Ingredient LAPIS    = new Ingredient("lapis").setBlock(Blocks.LAPIS_BLOCK).setDensity(2400 /*avg*/).setMeltingPoint(1250).setBoilingPoint(2750)/*not enough info on compound. Used estimates for feldspathoids*/;
    public static final Ingredient STONE    = new Ingredient("stone").setBlock(Blocks.STONE).setDensity(3011).setMeltingPoint(1395).setBoilingPoint(2503); /*treated as basalt*/

    static
    {
        registerIngredient(IRON);
        registerIngredient(GOLD);
        registerIngredient(DIAMOND);
        registerIngredient(COAL);
        registerIngredient(CHARCOAL);
        registerIngredient(LAPIS);
    }

    private IngredientRegistry() {
    }

    /**
     * Called by Forge to prepare the ID map for server -> client sync.
     * Modders, DO NOT call this.
     */
    public static void initIngredientIDs(BiMap<Ingredient, Integer> newingredientIDs, Set<String> defaultNames)
    {
        maxID = newingredientIDs.size();
        loadIngredientDefaults(newingredientIDs, defaultNames);
    }

    /**
     * Called by forge to load default ingredient IDs from the world or from server -> client for syncing
     * DO NOT call this and expect useful behaviour.
     *
     * @param localIngredientIDs
     * @param defaultNames
     */
    private static void loadIngredientDefaults(BiMap<Ingredient, Integer> localIngredientIDs, Set<String> defaultNames)
    {
        // If there's an empty set of default names, use the defaults as defined locally
        if (defaultNames.isEmpty())
        {
            defaultNames.addAll(defaultIngredientName.values());
        }
        BiMap<String, Ingredient> localIngredients = HashBiMap.create(ingredients);
        for (String defaultName : defaultNames)
        {
            Ingredient ingredient = masterIngredientReference.get(defaultName);
            if (ingredient == null)
            {
                String derivedName = defaultName.split(":", 2)[1];
                String localDefault = defaultIngredientName.get(derivedName);
                if (localDefault == null)
                {
                    FMLLog.getLogger().log(Level.ERROR, "The ingredient {} (specified as {}) is missing from this instance - it will be removed", derivedName, defaultName);
                    continue;
                }
                ingredient = masterIngredientReference.get(localDefault);
                FMLLog.getLogger().log(Level.ERROR, "The ingredient {} specified as default is not present - it will be reverted to default {}", defaultName, localDefault);
            }
            FMLLog.getLogger().log(Level.DEBUG, "The ingredient {} has been selected as the default ingredient for {}", defaultName, ingredient.getName());
            Ingredient oldIngredient = localIngredients.put(ingredient.getName(), ingredient);
            Integer id = localIngredientIDs.remove(oldIngredient);
            localIngredientIDs.put(ingredient, id);
        }
        BiMap<Integer, String> localIngredientNames = HashBiMap.create();
        for (Map.Entry<Ingredient, Integer> e : localIngredientIDs.entrySet())
        {
            localIngredientNames.put(e.getValue(), e.getKey().getName());
        }
        ingredientIDs = localIngredientIDs;
        ingredients = localIngredients;
        ingredientNames = localIngredientNames;
        ingredientBlocks = null;
        for (IngredientDelegate fd : delegates.values())
        {
            fd.rebind();
        }
    }

    /**
     * Register a new Ingredient. If a ingredient with the same name already exists, registration the alternative ingredient is tracked
     * in case it is the default in another place
     *
     * @param ingredient The ingredient to register.
     * @return True if the ingredient was registered as the current default ingredient, false if it was only registered as an alternative
     */
    public static boolean registerIngredient(Ingredient ingredient)
    {
        masterIngredientReference.put(uniqueName(ingredient), ingredient);
        delegates.put(ingredient, new IngredientDelegate(ingredient, ingredient.getName()));
        if (ingredients.containsKey(ingredient.getName()))
        {
            return false;
        }
        ingredients.put(ingredient.getName(), ingredient);
        maxID++;
        ingredientIDs.put(ingredient, maxID);
        ingredientNames.put(maxID, ingredient.getName());
        defaultIngredientName.put(ingredient.getName(), uniqueName(ingredient));

        MinecraftForge.EVENT_BUS.post(new IngredientRegisterEvent(ingredient.getName(), maxID));
        return true;
    }

    private static String uniqueName(Ingredient ingredient)
    {
        ModContainer activeModContainer = Loader.instance().activeModContainer();
        String activeModContainerName = activeModContainer == null ? "minecraft" : activeModContainer.getModId();
        return activeModContainerName + ":" + ingredient.getName();
    }

    /**
     * Is the supplied ingredient the current default ingredient for it's name
     *
     * @param ingredient the ingredient we're testing
     * @return if the ingredient is default
     */
    public static boolean isIngredientDefault(Ingredient ingredient)
    {
        return ingredients.containsValue(ingredient);
    }

    /**
     * Does the supplied ingredient have an entry for it's name (whether or not the ingredient itself is default)
     *
     * @param ingredient the ingredient we're testing
     * @return if the ingredient's name has a registration entry
     */
    public static boolean isIngredientRegistered(Ingredient ingredient)
    {
        return ingredient != null && ingredients.containsKey(ingredient.getName());
    }

    public static boolean isIngredientRegistered(String ingredientName)
    {
        return ingredients.containsKey(ingredientName);
    }

    public static Ingredient getIngredient(String ingredientName)
    {
        return ingredients.get(ingredientName);
    }

    @Deprecated // Modders should never actually use int ID, use String
    public static Ingredient getIngredient(int ingredientID)
    {
        return ingredientIDs.inverse().get(ingredientID);
    }

    @Deprecated // Modders should never actually use int ID, use String
    public static int getIngredientID(Ingredient ingredient)
    {
        Integer ret = ingredientIDs.get(ingredient);
        if (ret == null)
            throw new RuntimeException("Attempted to access ID for unregistered ingredient, Stop using this method modder!");
        return ret;
    }

    @Deprecated // Modders should never actually use int ID, use String
    public static int getIngredientID(String ingredientName)
    {
        Integer ret = ingredientIDs.get(getIngredient(ingredientName));
        if (ret == null)
            throw new RuntimeException("Attempted to access ID for unregistered ingredient, Stop using this method modder!");
        return ret;
    }

    public static String getIngredientName(Ingredient ingredient)
    {
        return ingredients.inverse().get(ingredient);
    }

    public static String getIngredientName(IngredientStack stack)
    {
        return getIngredientName(stack.getIngredient());
    }

    public static IngredientStack getIngredientStack(String ingredientName, int amount) {
        if (!ingredients.containsKey(ingredientName)) {
            return null;
        }
        return new IngredientStack(getIngredient(ingredientName), amount);
    }

    /**
     * Returns a read-only map containing Ingredient Names and their associated Ingredients.
     */
    public static Map<String, Ingredient> getRegisteredIngredients() {
        return ImmutableMap.copyOf(ingredients);
    }

    /**
     * Returns a read-only map containing Ingredient Names and their associated IDs.
     * Modders should never actually use this, use the String names.
     */
    @Deprecated
    public static Map<Ingredient, Integer> getRegisteredIngredientIDs() {
        return ImmutableMap.copyOf(ingredientIDs);
    }


    public static class IngredientRegisterEvent extends Event {
        private final String ingredientName;
        private final int ingredientID;

        public IngredientRegisterEvent(String ingredientName, int ingredientID) {
            this.ingredientName = ingredientName;
            this.ingredientID = ingredientID;
        }

        public String getIngredientName() {
            return ingredientName;
        }

        public int getIngredientID() {
            return ingredientID;
        }
    }

    public static int getMaxID() {
        return maxID;
    }

    public static String getDefaultIngredientName(Ingredient key) {
        String name = masterIngredientReference.inverse().get(key);
        if (Strings.isNullOrEmpty(name)) {
            FMLLog.getLogger().log(Level.ERROR, "The ingredient registry is corrupted. A ingredient {} {} is not properly registered. The mod that registered this is broken", key.getClass().getName(), key.getName());
            throw new IllegalStateException("The ingredient registry is corrupted");
        }
        return name;
    }

    public static void loadIngredientDefaults(NBTTagCompound tag) {
        Set<String> defaults = Sets.newHashSet();
        if (tag.hasKey("DefaultIngredientList", 9)) {
            FMLLog.getLogger().log(Level.DEBUG, "Loading persistent ingredient defaults from world");
            NBTTagList tl = tag.getTagList("DefaultIngredientList", 8);
            for (int i = 0; i < tl.tagCount(); i++) {
                defaults.add(tl.getStringTagAt(i));
            }
        } else {
            FMLLog.getLogger().log(Level.DEBUG, "World is missing persistent ingredient defaults - using local defaults");
        }
        loadIngredientDefaults(HashBiMap.create(ingredientIDs), defaults);
    }

    public static void writeDefaultIngredientList(NBTTagCompound forgeData) {
        NBTTagList tagList = new NBTTagList();

        for (Map.Entry<String, Ingredient> def : ingredients.entrySet()) {
            tagList.appendTag(new NBTTagString(getDefaultIngredientName(def.getValue())));
        }

        forgeData.setTag("DefaultIngredientList", tagList);
    }

    public static void validateIngredientRegistry() {
        Set<Ingredient> illegalIngredients = Sets.newHashSet();
        for (Ingredient f : ingredients.values()) {
            if (!masterIngredientReference.containsValue(f)) {
                illegalIngredients.add(f);
            }
        }

        if (!illegalIngredients.isEmpty()) {
            FMLLog.getLogger().log(Level.FATAL, "The ingredient registry is corrupted. Something has inserted a ingredient without registering it");
            FMLLog.getLogger().log(Level.FATAL, "There is {} unregistered ingredients", illegalIngredients.size());
            for (Ingredient f : illegalIngredients) {
                FMLLog.getLogger().log(Level.FATAL, "  Ingredient name : {}, type: {}", f.getName(), f.getClass().getName());
            }
            FMLLog.getLogger().log(Level.FATAL, "The mods that own these ingredients need to register them properly");
            throw new IllegalStateException("The ingredient map contains ingredients unknown to the master ingredient registry");
        }
    }

    static RegistryDelegate<Ingredient> makeDelegate(Ingredient fl) {
        return delegates.get(fl);
    }


    private static class IngredientDelegate implements RegistryDelegate<Ingredient> {
        private String name;
        private Ingredient ingredient;

        IngredientDelegate(Ingredient ingredient, String name) {
            this.ingredient = ingredient;
            this.name = name;
        }

        @Override
        public Ingredient get() {
            return ingredient;
        }

        @Override
        public ResourceLocation name() {
            return new ResourceLocation(name);
        }

        @Override
        public Class<Ingredient> type() {
            return Ingredient.class;
        }

        void rebind() {
            ingredient = ingredients.get(name);
        }


    }
}
