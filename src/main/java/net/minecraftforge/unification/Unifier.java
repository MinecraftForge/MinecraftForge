/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.unification;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * The {@link Unifier} allows mods to register basic materials so they can be unified.
 * <p>
 * Using the {@link Unifier}, everything can return a single unified type,
 * so for example players will only ever get one kind of Copper Ingot or Tin Ore.
 * <p>
 * Basic materials follow vanilla conventions, like iron ore being smeltable into iron ingots.
 * If your materials can't be 100% replaced by a version from other mods, do not register them for unification.
 * <p>
 * <b>Usage:</b>
 * <p>
 * First, create your {@link UnifiedMaterial}s and {@link UnifiedForm}s or use the ones listed in {@link UnificationConstants}.
 * <p>
 * Then, add your mod's variant in preInit with {@link #add(UnifiedMaterial, UnifiedForm, T)}.
 * After preInit, get instances of {@link Unified} with {@link #get(UnifiedMaterial, UnifiedForm)},
 * which is used to unify all things with those properties.
 * <p>
 * <b>Recipes:</b>
 * <p>
 * Recipes created with {@link Unified} for an ingredient will accept any item registered for a type, and return just the one unified type.
 * {@link Unified} can be used in recipes like {@link ShapedOreRecipe} and {@link ShapelessOreRecipe},
 * or you can implement your own recipes.
 * <p>
 * You can also create JSON recipes that use {@link Unified} as an ingredient or result, by using
 * the recipe type "forge:ore_shaped" or "forge:ore_shapeless", and using the ingredient format
 * {"type":"forge:unified", "material": "materialUid", "form": "formUid"}
 * For examples see the json recipes in the forge test directory "src/test/resources/assets/unification_test/recipes"
 */
public final class Unifier<T>
{
    private final Map<UnifiedMaterial, Map<UnifiedForm, Unified<T>>> UNIFICATION_TABLE = new IdentityHashMap<>();
    private final Consumer<T> validator;
    private final Comparator<T> sortOrder;
    private final Function<T, ResourceLocation> namer;
    private final Function<T, T> copier;
    private final boolean enforceLoaderState;

    /**
     * Create a new instance with {@link UnificationManager#createUnifier(Consumer, Function, Function, boolean)}
     */
    Unifier(Consumer<T> validator, Function<T, ResourceLocation> namer, Function<T, T> copier, boolean enforceLoaderState)
    {
        this.validator = validator;
        this.sortOrder = new VariantSortOrder<>(namer);
        this.namer = namer;
        this.copier = copier;
        this.enforceLoaderState = enforceLoaderState;
    }

    /**
     * Add a variant to the unifier during preInit.
     * If no {@link Unified} exists yet, one is created.
     * If a {@link Unified} already exist with these properties, the variant is added to its list of variants.
     * For example usage, see {@link UnificationConstants#initVanillaEntries()}
     */
    public void add(UnifiedMaterial material, UnifiedForm form, T variant)
    {
        if (enforceLoaderState)
        {
            LoaderState loaderState = Loader.instance().getLoaderState();
            Preconditions.checkState(loaderState.ordinal() < LoaderState.INITIALIZATION.ordinal(), "variants should only be added before Init");
        }
        Preconditions.checkNotNull(material, "material must not be null");
        Preconditions.checkNotNull(form, "form must not be null");
        Preconditions.checkNotNull(variant, "variant must not be null");

        variant = copier.apply(variant);
        validator.accept(variant);
        ResourceLocation name = namer.apply(variant);
        Preconditions.checkNotNull(name, "variant name must not be null");

        Map<UnifiedForm, Unified<T>> map = UNIFICATION_TABLE.computeIfAbsent(material, k -> new IdentityHashMap<>());
        Unified<T> unified = map.computeIfAbsent(form, k -> new Unified<>(material, form, sortOrder, copier));
        unified.addVariant(variant);
    }

    /**
     * @return a {@link Unified} with the given properties.
     * returns null if no {@link Unified} exists with those properties.
     * Must be called during init or later, so that all mods have a chance to add their variants in preInit first
     */
    @Nullable
    public Unified<T> get(UnifiedMaterial material, UnifiedForm form)
    {
        if (enforceLoaderState)
        {
            LoaderState loaderState = Loader.instance().getLoaderState();
            Preconditions.checkState(loaderState.ordinal() > LoaderState.PREINITIALIZATION.ordinal(), "you can only get unified after all mods have added their variants in preInit");
        }
        Preconditions.checkNotNull(material, "material must not be null");
        Preconditions.checkNotNull(form, "form must not be null");
        Map<UnifiedForm, Unified<T>> map = UNIFICATION_TABLE.get(material);
        if (map == null) return null;
        return map.get(form);
    }

    /**
     * @return a map of all the {@link UnifiedForm} added for a given {@link UnifiedMaterial}.
     */
    public Map<UnifiedForm, Unified<T>> getFormsMapForMaterial(UnifiedMaterial material)
    {
        Preconditions.checkNotNull(material, "material must not be null");
        Map<UnifiedForm, Unified<T>> map = UNIFICATION_TABLE.get(material);
        if (map == null) return Collections.emptyMap();
        return Collections.unmodifiableMap(map);
    }

    private static final class VariantSortOrder<T> implements Comparator<T>
    {
        private final Function<T, ResourceLocation> namer;

        private VariantSortOrder(Function<T, ResourceLocation> namer)
        {
            this.namer = namer;
        }

        @Override
        public int compare(T o1, T o2)
        {
            ResourceLocation name1 = namer.apply(o1);
            ResourceLocation name2 = namer.apply(o2);

            List<String> priorities = ForgeModContainer.getUnificationPriorityModIds();
            int priorityIdx1 = getPriority(name1.getResourceDomain(), priorities);
            int priorityIdx2 = getPriority(name2.getResourceDomain(), priorities);

            int priorityCompare = Integer.compare(priorityIdx1, priorityIdx2);
            if (priorityCompare != 0)
            {
                return priorityCompare;
            }
            return name1.toString().compareTo(name2.toString());
        }

        private static int getPriority(String modId, List<String> priorities)
        {
            int priority = priorities.indexOf(modId);
            if (priority == -1)
            {
                return Integer.MAX_VALUE;
            }
            return priority;
        }
    }
}
