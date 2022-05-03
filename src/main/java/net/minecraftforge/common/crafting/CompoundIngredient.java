/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class CompoundIngredient extends Ingredient
{
    private List<Ingredient> children;
    private ItemStack[] stacks;
    private IntList itemIds;
    private final boolean isSimple;

    protected CompoundIngredient(List<Ingredient> children)
    {
        super(Stream.of());
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    @Nonnull
    public ItemStack[] getItems()
    {
        if (stacks == null)
        {
            List<ItemStack> tmp = Lists.newArrayList();
            for (Ingredient child : children)
                Collections.addAll(tmp, child.getItems());
            stacks = tmp.toArray(new ItemStack[tmp.size()]);

        }
        return stacks;
    }

    @Override
    @Nonnull
    public IntList getStackingIds()
    {
        //TODO: Add a child.isInvalid()?
        if (this.itemIds == null)
        {
            this.itemIds = new IntArrayList();
            for (Ingredient child : children)
                this.itemIds.addAll(child.getStackingIds());
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.itemIds;
    }

    @Override
    public boolean test(@Nullable ItemStack target)
    {
        if (target == null)
            return false;

        return children.stream().anyMatch(c -> c.test(target));
    }

    @Override
    protected void invalidate()
    {
        this.itemIds = null;
        this.stacks = null;
        //Shouldn't need to invalidate children as this is only called form invalidateAll..
    }

    @Override
    public boolean isSimple()
    {
        return isSimple;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    @Nonnull
    public Collection<Ingredient> getChildren()
    {
        return this.children;
    }

    @Override
    public JsonElement toJson()
    {
       if (this.children.size() == 1)
       {
          return this.children.get(0).toJson();
       }
       else
       {
          JsonArray json = new JsonArray();
          this.children.stream().forEach(e -> json.add(e.toJson()));
          return json;
       }
    }

    @Override
    public boolean isEmpty()
    {
        return getItems().length == 0;
    }

    public static class Serializer implements IIngredientSerializer<CompoundIngredient>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public CompoundIngredient parse(PacketBuffer buffer)
        {
            return new CompoundIngredient(Stream.generate(() -> Ingredient.fromNetwork(buffer)).limit(buffer.readVarInt()).collect(Collectors.toList()));
        }

        @Override
        public CompoundIngredient parse(JsonObject json)
        {
            throw new JsonSyntaxException("CompoundIngredient should not be directly referenced in json, just use an array of ingredients.");
        }

        @Override
        public void write(PacketBuffer buffer, CompoundIngredient ingredient)
        {
            buffer.writeVarInt(ingredient.children.size());
            ingredient.children.forEach(c -> c.toNetwork(buffer));
        }

    }
}
