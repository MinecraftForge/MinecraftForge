package net.minecraftforge.common;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

import net.minecraft.world.WorldProvider;

/**
 * Handles tags for dimensions, in a way similar to {@link net.minecraftforge.common.BiomeDictionary}.
 * <p/>
 * A type can have subtypes and supertypes.
 * A dimension matches a type when it is associated with either the type or any of its sub-types.
 */
public class DimensionDictionary
{
    /**
     * Dimensions that are not registered in the dictionary default to this category.
     * Assigning this to your dimension manually is discouraged.
     */
    public static final String UNCATEGORIZED = "UNCATEGORIZED";

    /**
     * Dimensions that exist in the base game of Minecraft.
     */
    public static final String MC_DEFAULT = "MC_DEFAULT";

    /**
     * Dimensions that are deemed to be situated in physical reality.
     */
    public static final String REAL = "REAL";
    /**
     * Dimensions that are not strictly situated in physical reality.
     */
    public static final String UNREAL = "UNREAL";
    /**
     * Dimensions made up in the mind of an entity or player. (e.g. his mind)
     */
    public static final String IMAGINARY = "IMAGINARY";
    /**
     * Dimensions that exists inside a digital space (e.g. computer)
     */
    public static final String SIMULATED = "SIMULATED";
    /**
     * Dimensions that are abstract and don't really exist at all.
     */
    public static final String ABSTRACT = "ABSTRACT";
    
    /**
     * Dimensions with a top limitation (e.g. bedrock), conveying that there is terrain beyond.
     */
    public static final String TOP_LIMIT = "TOP_LIMIT";
    /**
     * Dimensions without a top limitation (e.g. bedrock), conveying that there is no terrain (void) beyond.
     */
    public static final String NO_TOP_LIMIT = "NO_TOP_LIMIT";
    /**
     * Dimensions with a bottom limitation (e.g. bedrock), conveying that there is terrain beyond.
     */
    public static final String BOTTOM_LIMIT = "BOTTOM_LIMIT";
    /**
     * Dimensions without a bottom limitation (e.g. bedrock), conveying that there is no terrain (void) beyond.
     */
    public static final String NO_BOTTOM_LIMIT = "NO_BOTTOM_LIMIT";
    
    /**
     * Dimensions that are only defined in a limited space, e.g. dungeon instances or boss arenas.
     */
    public static final String FINITE = "FINITE";
    /**
     * Dimensions that are not limited in space, e.g. any dynamically generated world.
     */
    public static final String INFINITE = "INFINITE";

    /**
     * Dimensions that are deemed to be on the surface of a planet.
     */
    public static final String PLANET_SURFACE = "PLANET_SURFACE";

    /**
     * Dimensions that are 'earth' themed, e.g. the overworld.
     */
    public static final String EARTH = "EARTH";
    /**
     * Dimensions that are 'hell' themed - e.g. netherrack, lava, fire.
     */
    public static final String HELL = "HELL";
    /**
     * Dimensions that are 'ender' themed - e.g. Endstone, Endermen
     */
    public static final String ENDER = "ENDER";
    /**
     * Dimensions intended for a particular boss fight, e.g. the end.
     */
    public static final String BOSS_ARENA = "BOSS_ARENA";

    private static final TIntObjectMap<Set<String>> dimensionTypes = new TIntObjectHashMap<Set<String>>();
    private static final Map<String, Type> types = new HashMap<String, Type>();

    private static final Set<String> SET_UNCATEGORIZED = Collections.singleton(UNCATEGORIZED);

    static
    {
        registerType(UNCATEGORIZED);

        registerSubtypes(UNREAL, Arrays.asList(IMAGINARY, SIMULATED, ABSTRACT));
        
        registerDimensionTypes(0, Arrays.asList(MC_DEFAULT, REAL, INFINITE, NO_TOP_LIMIT, BOTTOM_LIMIT, PLANET_SURFACE, EARTH));
        registerDimensionTypes(-1, Arrays.asList(MC_DEFAULT, REAL, INFINITE, TOP_LIMIT, BOTTOM_LIMIT, HELL));
        registerDimensionTypes(1, Arrays.asList(MC_DEFAULT, REAL, FINITE, NO_TOP_LIMIT, NO_BOTTOM_LIMIT, BOSS_ARENA, ENDER));
    }

    /**
     * Registers a dimension for dimension types.
     * This will also register each of the used types, if they weren't already.
     *
     * @param dimensionID The ID of the dimension.
     * @param types       The dimension types.
     */
    public static void registerDimensionTypes(int dimensionID, Collection<String> types)
    {
        Set<String> dTypes = dimensionTypes.get(dimensionID);
        if (dTypes == null)
        {
            dTypes = new HashSet<String>();
            dimensionTypes.put(dimensionID, dTypes);
        }
        dTypes.addAll(types);

        for (String type : types)
            registerType(type);
    }

    /**
     * Unregisters a dimension from certain dimension types.
     * This is useful for cleanup if a dimension changes type or is destructed.
     *
     * @param dimensionID The ID of the dimension.
     * @param types       The types to unregister, or null to unregister all types.
     */
    public static void unregisterDimensionTypes(int dimensionID, Collection<String> types)
    {
        Set<String> dTypes = dimensionTypes.get(dimensionID);
        if (dTypes != null)
        {
            if (types == null)
                dTypes.clear();
            else
                dTypes.removeAll(types);
        }
    }

    /**
     * Registers a dimension type.
     *
     * @param type The type to register.
     */
    public static void registerType(String type)
    {
        if (!types.containsKey(type))
            types.put(type, new Type());
    }

    /**
     * Registers many dimension types.
     *
     * @param types The types to register.
     */
    public static void registerTypes(Collection<String> types)
    {
        for (String type : types)
            registerType(type);
    }

    /**
     * Adds subtypes to a specific type.
     * This will also register any used types, if they weren't already.
     *
     * @param type     The type.
     * @param subtypes The subtypes.
     */
    public static void registerSubtypes(String type, Collection<String> subtypes)
    {
        registerGetType(type).subtypes.addAll(subtypes);

        for (String sub : subtypes)
            registerGetType(sub).supertypes.add(type);
    }

    /**
     * Adds supertypes to a specific type.
     * This will also register any used types, if they weren't already.
     *
     * @param type     The type.
     * @param supertypes The supertypes.
     */
    public static void registerSupertypes(String type, Collection<String> supertypes)
    {
        registerGetType(type).supertypes.addAll(supertypes);

        for (String supertype : supertypes)
            registerGetType(supertype).subtypes.add(type);
    }

    /**
     * Returns a set of types a specific dimension was registered for.
     *
     * @param provider The dimension's provider.
     * @return The dimension's types.
     */
    public static Set<String> getDimensionTypes(WorldProvider provider)
    {
        if (provider instanceof Handler)
            return ((Handler) provider).getDimensionTypes();
            
        Set<String> types = dimensionTypes.get(provider.dimensionId);
        return types != null ? types : SET_UNCATEGORIZED;
    }

    /**
     * Determines if a dimension matches all types from a collection of types.
     *
     * @param provider The dimension's provider.
     * @param types       The types.
     * @return True if all types were matched, otherwise false.
     */
    public static boolean dimensionMatchesAllTypes(WorldProvider provider, Collection<String> types)
    {
        for (String type : types)
        {
            if (!dimensionMatchesType(provider, type))
                return false;
        }

        return true;
    }

    /**
     * Determines if a dimension matches a specific type.
     * This is the case exactly when the dimension is associated with either the type or any of its subtypes.
     *
     * @param provider The dimension's provider.
     * @param type        The type.
     * @return True if the dimension matches the type, otherwise false.
     */
    public static boolean dimensionMatchesType(WorldProvider provider, String type)
    {
        Set<String> dimTypes = getDimensionTypes(provider);

        Queue<String> curTypes = new ArrayDeque<String>();

        do
        {
            if (dimTypes.contains(type))
                return true;

            Type curT = types.get(type);
            if (curT != null)
                curTypes.addAll(curT.subtypes);
        }
        while ((type = curTypes.poll()) != null);

        return false;
    }

    /**
     * Returns a type's supertypes.
     *
     * @param type The type.
     * @return The type's supertypes.
     */
    public static Set<String> getSupertypes(String type)
    {
        Type t = types.get(type);
        return t == null ? Collections.<String>emptySet() : Collections.unmodifiableSet(t.supertypes);
    }

    /**
     * Returns a type's subtypes.
     *
     * @param type The type.
     * @return The type's subtypes.
     */
    public static Set<String> getSubtypes(String type)
    {
        Type t = types.get(type);
        return t == null ? Collections.<String>emptySet() : Collections.unmodifiableSet(t.subtypes);
    }
    
    /**
     * Returns a set of all registered types.
     * @return A set of all registered types.
     */
    public static Set<String> allRegisteredTypes()
    {
        return types.keySet();
    }

    private static Type registerGetType(String type)
    {
        registerType(type);
        return types.get(type);
    }
    
    public static interface Handler
    {
        /**
         * Note: Do not invoke this directly. Use {@link DimensionDictionary}'s methods instead.
         * @return The dimension types.
         */
        Set<String> getDimensionTypes();
    }

    private static class Type
    {
        public final Set<String> supertypes = new HashSet<String>();
        public final Set<String> subtypes = new HashSet<String>();
    }
}
