/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.common.data;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public final class ForgeRecipeProvider extends RecipeProvider
{
    private Map<Item, Tag<Item>> replacements = new HashMap<>();
    private Set<ResourceLocation> excludes = new HashSet<>();

    public ForgeRecipeProvider(DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    private void exclude(ItemLike item)
    {
        excludes.add(item.asItem().getRegistryName());
    }

    private void replace(ItemLike item, Tag<Item> tag)
    {
        replacements.put(item.asItem(), tag);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        replace(Items.STICK,        Tags.Items.RODS_WOODEN);
        replace(Items.GOLD_INGOT,   Tags.Items.INGOTS_GOLD);
        replace(Items.IRON_INGOT,   Tags.Items.INGOTS_IRON);
        replace(Items.NETHERITE_INGOT, Tags.Items.INGOTS_NETHERITE);
        replace(Items.DIAMOND,      Tags.Items.GEMS_DIAMOND);
        replace(Items.EMERALD,      Tags.Items.GEMS_EMERALD);
        replace(Items.CHEST,        Tags.Items.CHESTS_WOODEN);
        replace(Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);

        exclude(Blocks.GOLD_BLOCK);
        exclude(Items.GOLD_NUGGET);
        exclude(Blocks.IRON_BLOCK);
        exclude(Items.IRON_NUGGET);
        exclude(Blocks.DIAMOND_BLOCK);
        exclude(Blocks.EMERALD_BLOCK);
        exclude(Blocks.NETHERITE_BLOCK);

        exclude(Blocks.COBBLESTONE_STAIRS);
        exclude(Blocks.COBBLESTONE_SLAB);
        exclude(Blocks.COBBLESTONE_WALL);

        super.buildCraftingRecipes(vanilla -> {
            FinishedRecipe modified = enhance(vanilla);
            if (modified != null)
                consumer.accept(modified);
        });

        buildVanillaBrewingRecipes(consumer);
    }

    private void buildVanillaBrewingRecipes(final Consumer<FinishedRecipe> consumer)
    {
        addContainerRecipe(consumer, Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        addContainerRecipe(consumer, Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        addMixingRecipe(consumer, Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Tags.Items.DUSTS_GLOWSTONE, Potions.THICK);
        addMixingRecipe(consumer, Potions.WATER, Tags.Items.DUSTS_REDSTONE, Potions.MUNDANE);
        addMixingRecipe(consumer, Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        addMixingRecipe(consumer, Potions.NIGHT_VISION, Tags.Items.DUSTS_REDSTONE, Potions.LONG_NIGHT_VISION);
        addMixingRecipe(consumer, Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        addMixingRecipe(consumer, Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        addMixingRecipe(consumer, Potions.INVISIBILITY, Tags.Items.DUSTS_REDSTONE, Potions.LONG_INVISIBILITY);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        addMixingRecipe(consumer, Potions.FIRE_RESISTANCE, Tags.Items.DUSTS_REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        addMixingRecipe(consumer, Potions.LEAPING, Tags.Items.DUSTS_REDSTONE, Potions.LONG_LEAPING);
        addMixingRecipe(consumer, Potions.LEAPING, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_LEAPING);
        addMixingRecipe(consumer, Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMixingRecipe(consumer, Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMixingRecipe(consumer, Potions.SLOWNESS, Tags.Items.DUSTS_REDSTONE, Potions.LONG_SLOWNESS);
        addMixingRecipe(consumer, Potions.SLOWNESS, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_SLOWNESS);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        addMixingRecipe(consumer, Potions.TURTLE_MASTER, Tags.Items.DUSTS_REDSTONE, Potions.LONG_TURTLE_MASTER);
        addMixingRecipe(consumer, Potions.TURTLE_MASTER, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_TURTLE_MASTER);
        addMixingRecipe(consumer, Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMixingRecipe(consumer, Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        addMixingRecipe(consumer, Potions.SWIFTNESS, Tags.Items.DUSTS_REDSTONE, Potions.LONG_SWIFTNESS);
        addMixingRecipe(consumer, Potions.SWIFTNESS, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_SWIFTNESS);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        addMixingRecipe(consumer, Potions.WATER_BREATHING, Tags.Items.DUSTS_REDSTONE, Potions.LONG_WATER_BREATHING);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        addMixingRecipe(consumer, Potions.HEALING, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_HEALING);
        addMixingRecipe(consumer, Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMixingRecipe(consumer, Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMixingRecipe(consumer, Potions.HARMING, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_HARMING);
        addMixingRecipe(consumer, Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMixingRecipe(consumer, Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMixingRecipe(consumer, Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        addMixingRecipe(consumer, Potions.POISON, Tags.Items.DUSTS_REDSTONE, Potions.LONG_POISON);
        addMixingRecipe(consumer, Potions.POISON, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_POISON);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        addMixingRecipe(consumer, Potions.REGENERATION, Tags.Items.DUSTS_REDSTONE, Potions.LONG_REGENERATION);
        addMixingRecipe(consumer, Potions.REGENERATION, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_REGENERATION);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        addMixingRecipe(consumer, Potions.STRENGTH, Tags.Items.DUSTS_REDSTONE, Potions.LONG_STRENGTH);
        addMixingRecipe(consumer, Potions.STRENGTH, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_STRENGTH);
        addMixingRecipe(consumer, Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        addMixingRecipe(consumer, Potions.WEAKNESS, Tags.Items.DUSTS_REDSTONE, Potions.LONG_WEAKNESS);
        addMixingRecipe(consumer, Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        addMixingRecipe(consumer, Potions.SLOW_FALLING, Tags.Items.DUSTS_REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    private void addMixingRecipe(final Consumer<FinishedRecipe> consumer, final Potion input, final Item ingredient, final Potion output)
    {
        String name = "brewing/" + input.getRegistryName().getPath() + "_" +
                ingredient.getRegistryName().getPath() + "_" +
                output.getRegistryName().getPath();
        BrewingRecipeBuilder.mixing(input, ingredient, output)
                .save(consumer, new ResourceLocation(output.getRegistryName().getNamespace(), name));
    }

    private void addMixingRecipe(final Consumer<FinishedRecipe> consumer, final Potion input, final Tag.Named<Item> ingredient, final Potion output)
    {
        String name = "brewing/" + input.getRegistryName().getPath() + "_" +
                ingredient.getName().getPath().replace('/', '_') + "_" +
                output.getRegistryName().getPath();
        BrewingRecipeBuilder.mixing(input, ingredient, output)
                .save(consumer, new ResourceLocation(output.getRegistryName().getNamespace(), name));
    }

    private void addContainerRecipe(final Consumer<FinishedRecipe> consumer, final Item input, final Tag.Named<Item> ingredient, final Item output)
    {
        String name = "brewing/" + input.getRegistryName().getPath() + "_" +
                ingredient.getName().getPath() + "_" +
                output.getRegistryName().getPath();
        BrewingRecipeBuilder.container(input, ingredient, output)
                .save(consumer, new ResourceLocation(output.getRegistryName().getNamespace(), name));
    }

    private void addContainerRecipe(final Consumer<FinishedRecipe> consumer, final Item input, final Item ingredient, final Item output)
    {
        String name = "brewing/" + input.getRegistryName().getPath() + "_" +
                ingredient.getRegistryName().getPath() + "_" +
                output.getRegistryName().getPath();
        BrewingRecipeBuilder.container(input, ingredient, output)
                .save(consumer, new ResourceLocation(output.getRegistryName().getNamespace(), name));
    }

    @Override
    protected void saveAdvancement(HashCache cache, JsonObject advancementJson, Path pathIn) {
        //NOOP - We dont replace any of the advancement things yet...
    }

    private FinishedRecipe enhance(FinishedRecipe vanilla)
    {
        if (vanilla instanceof ShapelessRecipeBuilder.Result)
            return enhance((ShapelessRecipeBuilder.Result)vanilla);
        if (vanilla instanceof ShapedRecipeBuilder.Result)
            return enhance((ShapedRecipeBuilder.Result)vanilla);
        return null;
    }

    private FinishedRecipe enhance(ShapelessRecipeBuilder.Result vanilla)
    {
        List<Ingredient> ingredients = getField(ShapelessRecipeBuilder.Result.class, vanilla, 4);
        boolean modified = false;
        for (int x = 0; x < ingredients.size(); x++)
        {
            Ingredient ing = enhance(vanilla.getId(), ingredients.get(x));
            if (ing != null)
            {
                ingredients.set(x, ing);
                modified = true;
            }
        }
        return modified ? vanilla : null;
    }

    private FinishedRecipe enhance(ShapedRecipeBuilder.Result vanilla)
    {
        Map<Character, Ingredient> ingredients = getField(ShapedRecipeBuilder.Result.class, vanilla, 5);
        boolean modified = false;
        for (Character x : ingredients.keySet())
        {
            Ingredient ing = enhance(vanilla.getId(), ingredients.get(x));
            if (ing != null)
            {
                ingredients.put(x, ing);
                modified = true;
            }
        }
        return modified ? vanilla : null;
    }

    private Ingredient enhance(ResourceLocation name, Ingredient vanilla)
    {
        if (excludes.contains(name))
            return null;

        boolean modified = false;
        List<Value> items = new ArrayList<>();
        Value[] vanillaItems = getField(Ingredient.class, vanilla, 2); //This will probably crash between versions, if null fix index
        for (Value entry : vanillaItems)
        {
            if (entry instanceof ItemValue)
            {
                ItemStack stack = entry.getItems().stream().findFirst().orElse(ItemStack.EMPTY);
                Tag<Item> replacement = replacements.get(stack.getItem());
                if (replacement != null)
                {
                    items.add(new TagValue(replacement));
                    modified = true;
                }
                else
                    items.add(entry);
            }
            else
                items.add(entry);
        }
        return modified ? Ingredient.fromValues(items.stream()) : null;
    }

    @SuppressWarnings("unchecked")
    private <T, R> R getField(Class<T> clz, T inst, int index)
    {
        Field fld = clz.getDeclaredFields()[index];
        fld.setAccessible(true);
        try
        {
            return (R)fld.get(inst);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
