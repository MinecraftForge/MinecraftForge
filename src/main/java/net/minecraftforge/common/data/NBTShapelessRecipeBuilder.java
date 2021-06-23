package net.minecraftforge.common.data;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class NBTShapelessRecipeBuilder
{
    private final Item result;
    private final int count;
    private final CompoundNBT compound;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;

    private NBTShapelessRecipeBuilder(IItemProvider result, int count, CompoundNBT compound)
    {
        this.result = result.asItem();
        this.count = count;
        this.compound = compound;
    }

    public static NBTShapelessRecipeBuilder shapeless(ItemStack stack)
    {
        return new NBTShapelessRecipeBuilder(stack.getItem(), stack.getCount(), stack.getTag());
    }

    public static NBTShapelessRecipeBuilder shapeless(IItemProvider result)
    {
        return new NBTShapelessRecipeBuilder(result, 1, null);
    }

    public static NBTShapelessRecipeBuilder shapeless(IItemProvider result, int count)
    {
        return new NBTShapelessRecipeBuilder(result, count, null);
    }

    public static NBTShapelessRecipeBuilder shapeless(IItemProvider result, int count, CompoundNBT compound)
    {
        return new NBTShapelessRecipeBuilder(result, count, compound);
    }

    public NBTShapelessRecipeBuilder requires(ITag<Item> tag)
    {
        return this.requires(Ingredient.of(tag));
    }

    public NBTShapelessRecipeBuilder requires(IItemProvider item)
    {
        return this.requires(item, 1);
    }

    public NBTShapelessRecipeBuilder requires(IItemProvider item, int quantity)
    {
        for (int i = 0; i < quantity; ++i)
        {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public NBTShapelessRecipeBuilder requires(Ingredient ingredient)
    {
        return this.requires(ingredient, 1);
    }

    public NBTShapelessRecipeBuilder requires(Ingredient ingredient, int quantity)
    {
        for (int i = 0; i < quantity; ++i)
        {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    public NBTShapelessRecipeBuilder unlockedBy(String name, ICriterionInstance criterion)
    {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    public NBTShapelessRecipeBuilder group(String group)
    {
        this.group = group;
        return this;
    }

    public void save(Consumer<IFinishedRecipe> consumer)
    {
        this.save(consumer, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
    }

    public void save(Consumer<IFinishedRecipe> consumer, String save)
    {
        ResourceLocation saveTo = new ResourceLocation(save);
        if (saveTo.equals(ForgeRegistries.ITEMS.getKey(this.result.getItem())))
        {
            throw new IllegalStateException("Shapeless Recipe " + save + " should remove its 'save' argument");
        } else
        {
            this.save(consumer, saveTo);
        }
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        this.ensureValid(id);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
        consumer.accept(new NBTShapelessRecipeBuilder.Result(id, this.result, this.count, this.compound, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
    }

    private void ensureValid(ResourceLocation id)
    {
        if (this.advancement.getCriteria().isEmpty())
        {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final CompoundNBT compound;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, CompoundNBT compound, String group, List<Ingredient> ingredients, Advancement.Builder advancementBuilder, ResourceLocation advancementId)
        {
            this.id = id;
            this.result = result;
            this.count = count;
            this.compound = compound;
            this.group = group;
            this.ingredients = ingredients;
            this.advancement = advancementBuilder;
            this.advancementId = advancementId;
        }

        public void serializeRecipeData(JsonObject json)
        {
            if (!this.group.isEmpty())
            {
                json.addProperty("group", this.group);
            }

            JsonArray ingredientsJson = new JsonArray();

            for (Ingredient ingredient : this.ingredients)
            {
                ingredientsJson.add(ingredient.toJson());
            }

            json.add("ingredients", ingredientsJson);
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (this.count > 1)
            {
                resultJson.addProperty("count", this.count);
            }
            if (this.compound != null)
            {
                resultJson.addProperty("nbt", NBTDynamicOps.INSTANCE.convertTo(JsonOps.INSTANCE, this.compound).toString());
            }

            json.add("result", resultJson);
        }

        public IRecipeSerializer<?> getType()
        {
            return IRecipeSerializer.SHAPELESS_RECIPE;
        }

        public ResourceLocation getId()
        {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement()
        {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId()
        {
            return this.advancementId;
        }
    }
}