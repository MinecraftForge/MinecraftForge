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

package net.minecraftforge.common.smelting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.custom_hash.TObjectFloatCustomHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Reflection;

/**
 * Registry for smelting recipes, should be used over the GameRegistry methods and vanilla's FurnaceRecipes class.
 * Awarded experience is determined based on output stack, <i>not</i> by recipe. This is due to the design of vanilla's TileEntityFurnace.
 */
public class SmeltingRecipeRegistry
{

    /**
     * Default time a recipe takes to cook as defined in the vanilla TileEntityFurnace::getCookTime method.
     */
    public static final int DEFAULT_SMELTING_TIME = 200;

    private static final List<SmeltingRecipe> recipes = new ArrayList<SmeltingRecipe>();
    private static final List<SmeltingRecipe> recipesUn = Collections.unmodifiableList(recipes);

    static
    {
        // ensure vanilla recipes are first
        Reflection.initialize(FurnaceRecipes.class);
    }

    /**
     * Add the given recipe.
     * This method does not register any kind of experience.
     *
     * @param recipe the recipe
     */
    public static void addRecipe(@Nonnull SmeltingRecipe recipe)
    {
        recipes.add(checkNotNull(recipe));
    }

    /**
     * Add the given recipe and set the given amount of experience for all it's outputs.
     *
     * @param recipe the recipe
     * @param xp     the amount of experience
     */
    public static void addRecipe(@Nonnull SmeltingRecipe recipe, float xp)
    {
        addRecipe(recipe);
        if (xp > 0)
        {
            for (ItemStack output : recipe.getPossibleOutputs())
            {
                setExperience(output, xp);
            }
        }
    }

    /**
     * Add a simple recipe that smelts the stacks matching the given input stack into the output stack.
     * Stack matching is done according to {@link OreDictionary#itemMatches(ItemStack, ItemStack, boolean)}
     * with {@code strict} set to false.
     *
     * @param input  the input stack
     * @param output the output stack
     */
    public static void addSimpleRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output)
    {
        addSimpleRecipe(input, output, 0, DEFAULT_SMELTING_TIME);
    }

    /**
     * Add a simple recipe that smelts the stacks matching the given input stack into the output stack
     * in the given duration.
     * Stack matching is done according to {@link OreDictionary#itemMatches(ItemStack, ItemStack, boolean)}
     * with {@code strict} set to false.
     *
     * @param input  the input stack
     * @param output the output stack
     * @param xp     the amount of experience to award for the output stack
     */
    public static void addSimpleRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output, float xp)
    {
        addSimpleRecipe(input, output, xp, DEFAULT_SMELTING_TIME);
    }

    /**
     * Add a simple recipe that smelts the stacks matching the given input stack into the output stack
     * in the given duration.
     * Stack matching is done according to {@link OreDictionary#itemMatches(ItemStack, ItemStack, boolean)}
     * with {@code strict} set to false.
     *
     * @param input    the input stack
     * @param output   the output stack
     * @param xp       the amount of experience to award for the output stack
     * @param duration the duration for this recipe in ticks
     */
    public static void addSimpleRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output, float xp, int duration)
    {
        addRecipe(new SimpleSmeltingRecipe(input, output, duration), xp);
    }

    /**
     * Add a recipe that smelts stacks matching the given OreDictionary name into the output stack.
     *
     * @param input  the OreDictionary name
     * @param output the output stack
     */
    public static void addOreRecipe(@Nonnull String input, @Nonnull ItemStack output)
    {
        addOreRecipe(input, output, 0, DEFAULT_SMELTING_TIME);
    }

    /**
     * Add a recipe that smelts stacks matching the given OreDictionary name into the output stack
     * in the given duration.
     *
     * @param input    the OreDictionary name
     * @param output   the output stack
     * @param xp       the amount of experience to award for the output stack
     */
    public static void addOreRecipe(@Nonnull String input, @Nonnull ItemStack output, float xp)
    {
        addOreRecipe(input, output, xp, DEFAULT_SMELTING_TIME);
    }

    /**
     * Add a recipe that smelts stacks matching the given OreDictionary name into the output stack
     * in the given duration.
     *
     * @param input    the OreDictionary name
     * @param output   the output stack
     * @param xp       the amount of experience to award for the output stack
     * @param duration the duration for this recipe in ticks
     */
    public static void addOreRecipe(@Nonnull String input, @Nonnull ItemStack output, float xp, int duration)
    {
        addRecipe(new OreSmeltingRecipe(input, output, duration), xp);
    }

    /**
     * Get an unmodifiable list of all smelting recipes. The list will update in-place as new recipes are added.
     *
     * @return the list of recipes
     */
    public static List<SmeltingRecipe> getRecipes()
    {
        return recipesUn;
    }

    /**
     * Get the first recipe matching the given input stack or null if no recipe matches.
     *
     * @param input the input stack
     * @return a matching recipe
     */
    @Nullable
    public static SmeltingRecipe getMatchingRecipe(@Nonnull ItemStack input)
    {
        for (SmeltingRecipe recipe : recipes)
        {
            if (recipe.matches(input))
            {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Get the output stack for the given input. The result is always a defensive copy.
     *
     * @param input the input stack
     * @return the output stack
     */
    @Nonnull
    public static ItemStack getOutput(ItemStack input)
    {
        SmeltingRecipe recipe = getMatchingRecipe(input);
        return recipe == null ? ItemStack.field_190927_a : recipe.getOutput(input);
    }

    /**
     * Get the duration the given input stack takes to smelt.
     *
     * @param input the input stack
     * @return the duration in ticks
     */
    public static int getDuration(ItemStack input)
    {
        for (SmeltingRecipe recipe : recipes)
        {
            if (recipe.matches(input))
            {
                return recipe.getDuration(input);
            }
        }
        return 0;
    }

    /**
     * Get the amount of experience awarded for taking the given output stack from a furnace.
     *
     * @param output the output stack
     * @return the amount of experience
     */
    public static float getExperience(@Nonnull ItemStack output)
    {
        return FurnaceRecipes.instance().getSmeltingExperience(output);
    }

    /**
     * Set the amount of experience awarded for taking the given output stack from a furnace.
     * The {@link OreDictionary#WILDCARD_VALUE wildcard metadata value} is supported.
     *
     * @param output the output stack
     * @param xp     the amount of experience
     */
    public static void setExperience(@Nonnull ItemStack output, float xp)
    {
        FurnaceRecipes.instance().setSmeltingExperience(output, xp);
    }

    /**
     * Get all possible output items. This can be useful for e.g. a statistics tracker.
     * This is not necessarily an exhaustive list, ItemStacks with different NBT tags might be collapsed into one ItemStack
     * without any NBT data.
     *
     * @return a list of all possible items
     */
    public static List<ItemStack> getAllPossibleOutputs()
    {
        ImmutableList.Builder<ItemStack> b = ImmutableList.builder();
        for (SmeltingRecipe recipe : recipes)
        {
            b.addAll(recipe.getPossibleOutputs());
        }
        return b.build();
    }
}
