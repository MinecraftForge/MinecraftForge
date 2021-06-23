package net.minecraftforge.common.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class NBTShapedRecipeBuilder
{
    private final Item result;
    private final int count;
    private final CompoundNBT compound;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;

    public NBTShapedRecipeBuilder(IItemProvider result, int count, CompoundNBT compound)
    {
        this.result = result.asItem();
        this.count = count;
        this.compound = compound;
    }

    public static NBTShapedRecipeBuilder shaped(ItemStack stack)
    {
        return new NBTShapedRecipeBuilder(stack.getItem(), stack.getCount(), stack.getTag());
    }

    public static NBTShapedRecipeBuilder shaped(IItemProvider result)
    {
        return new NBTShapedRecipeBuilder(result, 1, null);
    }

    public static NBTShapedRecipeBuilder shaped(IItemProvider result, int count)
    {
        return new NBTShapedRecipeBuilder(result, count, null);
    }

    public static NBTShapedRecipeBuilder shaped(IItemProvider result, int count, CompoundNBT compound)
    {
        return new NBTShapedRecipeBuilder(result, count, compound);
    }

    public NBTShapedRecipeBuilder define(Character symbol, ITag<Item> tag)
    {
        return this.define(symbol, Ingredient.of(tag));
    }

    public NBTShapedRecipeBuilder define(Character symbol, IItemProvider item)
    {
        return this.define(symbol, Ingredient.of(item));
    }

    public NBTShapedRecipeBuilder define(Character symbol, Ingredient item)
    {
        if (this.key.containsKey(symbol))
        {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ')
        {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else
        {
            this.key.put(symbol, item);
            return this;
        }
    }

    public NBTShapedRecipeBuilder pattern(String pattern)
    {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length())
        {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else
        {
            this.rows.add(pattern);
            return this;
        }
    }

    public NBTShapedRecipeBuilder unlockedBy(String name, ICriterionInstance criterion)
    {
        this.advancement.addCriterion(name, criterion);
        return this;
    }

    public NBTShapedRecipeBuilder group(String group)
    {
        this.group = group;
        return this;
    }

    public void save(Consumer<IFinishedRecipe> consumer)
    {
        this.save(consumer, ForgeRegistries.ITEMS.getKey(this.result));
    }

    public void save(Consumer<IFinishedRecipe> consumer, String save)
    {
        ResourceLocation saveTo = new ResourceLocation(save);
        if (saveTo.equals(ForgeRegistries.ITEMS.getKey(this.result.getItem())))
        {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else
        {
            this.save(consumer, saveTo);
        }
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        this.ensureValid(id);
        this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result, this.count, this.compound, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
    }

    private void ensureValid(ResourceLocation id)
    {
        if (this.rows.isEmpty())
        {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else
        {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for (String s : this.rows)
            {
                for (int i = 0; i < s.length(); ++i)
                {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ')
                    {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty())
            {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1)
            {
                throw new IllegalStateException("Shaped recipe " + id
                        + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.advancement.getCriteria().isEmpty())
            {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

    public class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final CompoundNBT compound;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, CompoundNBT compound, String group, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advancement, ResourceLocation advancementId)
        {
            this.id = id;
            this.result = result;
            this.count = count;
            this.compound = compound;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        public void serializeRecipeData(JsonObject json)
        {
            if (!this.group.isEmpty())
            {
                json.addProperty("group", this.group);
            }

            JsonArray patternJson = new JsonArray();

            for (String s : this.pattern)
            {
                patternJson.add(s);
            }

            json.add("pattern", patternJson);
            JsonObject keyJson = new JsonObject();

            for (Entry<Character, Ingredient> entry : this.key.entrySet())
            {
                keyJson.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }

            json.add("key", keyJson);
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
            return IRecipeSerializer.SHAPED_RECIPE;
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