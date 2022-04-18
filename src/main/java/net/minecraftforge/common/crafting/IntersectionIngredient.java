/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Ingredient that matches if all child ingredients match */
public class IntersectionIngredient extends AbstractIngredient
{
    private final List<Ingredient> children;
    private final boolean isSimple;
    private ItemStack[] intersectedMatchingStacks = null;
    private IntList packedMatchingStacks = null;
    protected IntersectionIngredient(List<Ingredient> children)
    {
        if (children.size() < 2)
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with one or no children");
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream().allMatch(Ingredient::isSimple);
    }

    /**
     * Gets an intersection ingredient
     * @param ingredients  List of ingredients to match
     * @return  Ingredient that only matches if all the passed ingredients match
     */
    public static Ingredient of(Ingredient... ingredients)
    {
        if (ingredients.length == 0)
            throw new IllegalArgumentException("Cannot create an IntersectionIngredient with no children, use Ingredient.of() to create an empty ingredient");
        if (ingredients.length == 1)
            return ingredients[0];

        return new IntersectionIngredient(Arrays.asList(ingredients));
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        if (stack == null || stack.isEmpty())
            return false;

        for (Ingredient ingredient : children)
            if (!ingredient.test(stack))
                return false;

        return true;
    }

    @Override
    public ItemStack[] getItems()
    {
        if (this.intersectedMatchingStacks == null)
        {
            this.intersectedMatchingStacks = Arrays
                    .stream(children.get(0).getItems())
                    .filter(stack ->
                        {
                            // the first ingredient is treated as a base, filtered by the second onwards
                            for (int i = 1; i < children.size(); i++)
                                if (!children.get(i).test(stack))
                                    return false;
                            return true;
                        })
                    .toArray(ItemStack[]::new);
        }
        return intersectedMatchingStacks;
    }

    @Override
    public boolean isEmpty()
    {
        return children.stream().anyMatch(Ingredient::isEmpty);
    }

    @Override
    public boolean isSimple()
    {
        return isSimple;
    }

    @Override
    protected void invalidate()
    {
        super.invalidate();
        this.intersectedMatchingStacks = null;
        this.packedMatchingStacks = null;
    }

    @Override
    public IntList getStackingIds()
    {
        if (this.packedMatchingStacks == null || checkInvalidation())
        {
            markValid();
            ItemStack[] matchingStacks = getItems();
            this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
            for(ItemStack stack : matchingStacks)
                this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));
            this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return packedMatchingStacks;
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        JsonArray array = new JsonArray();
        for (Ingredient ingredient : children)
            array.add(ingredient.toJson());

        json.add("children", array);
        return json;
    }

    @Override
    public IIngredientSerializer<IntersectionIngredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements IIngredientSerializer<IntersectionIngredient>
    {
        public static final IIngredientSerializer<IntersectionIngredient> INSTANCE = new Serializer();

        @Override
        public IntersectionIngredient parse(JsonObject json)
        {
            JsonArray children = GsonHelper.getAsJsonArray(json, "children");
            if (children.size() < 2)
                throw new JsonSyntaxException("Must have at least two children for an intersection ingredient");
            return new IntersectionIngredient(IntStream.range(0, children.size()).mapToObj(i -> Ingredient.fromJson(children.get(i))).toList());
        }

        @Override
        public IntersectionIngredient parse(FriendlyByteBuf buffer)
        {
            return new IntersectionIngredient(Stream.generate(() -> Ingredient.fromNetwork(buffer)).limit(buffer.readVarInt()).toList());
        }

        @Override
        public void write(FriendlyByteBuf buffer, IntersectionIngredient intersection)
        {
            buffer.writeVarInt(intersection.children.size());
            for (Ingredient ingredient : intersection.children)
                ingredient.toNetwork(buffer);
        }
    }
}
