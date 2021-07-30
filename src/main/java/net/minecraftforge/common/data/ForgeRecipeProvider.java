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
import com.mojang.datafixers.util.Either;
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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Triple;

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

    private final List<Triple<Item, Either<Item, Tag.Named<Item>>, Item>> containerRecipes = new ArrayList<>();
    private final List<Triple<Potion, Either<Item, Tag.Named<Item>>, Potion>> mixingRecipes = new ArrayList<>();
    private final Set<Item> containers = new HashSet<>();
    private final Set<Potion> potions = new HashSet<>();
    private void buildVanillaBrewingRecipes(Consumer<FinishedRecipe> consumer)
    {
        containers.add(Items.POTION);
        containers.add(Items.SPLASH_POTION);
        containers.add(Items.LINGERING_POTION);
        addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        addMixingRecipe(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Tags.Items.DUSTS_GLOWSTONE, Potions.THICK);
        addMixingRecipe(Potions.WATER, Tags.Items.DUSTS_REDSTONE, Potions.MUNDANE);
        addMixingRecipe(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        addMixingRecipe(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        addMixingRecipe(Potions.NIGHT_VISION, Tags.Items.DUSTS_REDSTONE, Potions.LONG_NIGHT_VISION);
        addMixingRecipe(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        addMixingRecipe(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        addMixingRecipe(Potions.INVISIBILITY, Tags.Items.DUSTS_REDSTONE, Potions.LONG_INVISIBILITY);
        addMixingRecipe(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        addMixingRecipe(Potions.FIRE_RESISTANCE, Tags.Items.DUSTS_REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        addMixingRecipe(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        addMixingRecipe(Potions.LEAPING, Tags.Items.DUSTS_REDSTONE, Potions.LONG_LEAPING);
        addMixingRecipe(Potions.LEAPING, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_LEAPING);
        addMixingRecipe(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMixingRecipe(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMixingRecipe(Potions.SLOWNESS, Tags.Items.DUSTS_REDSTONE, Potions.LONG_SLOWNESS);
        addMixingRecipe(Potions.SLOWNESS, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_SLOWNESS);
        addMixingRecipe(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        addMixingRecipe(Potions.TURTLE_MASTER, Tags.Items.DUSTS_REDSTONE, Potions.LONG_TURTLE_MASTER);
        addMixingRecipe(Potions.TURTLE_MASTER, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_TURTLE_MASTER);
        addMixingRecipe(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        addMixingRecipe(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        addMixingRecipe(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        addMixingRecipe(Potions.SWIFTNESS, Tags.Items.DUSTS_REDSTONE, Potions.LONG_SWIFTNESS);
        addMixingRecipe(Potions.SWIFTNESS, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_SWIFTNESS);
        addMixingRecipe(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        addMixingRecipe(Potions.WATER_BREATHING, Tags.Items.DUSTS_REDSTONE, Potions.LONG_WATER_BREATHING);
        addMixingRecipe(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        addMixingRecipe(Potions.HEALING, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_HEALING);
        addMixingRecipe(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMixingRecipe(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMixingRecipe(Potions.HARMING, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_HARMING);
        addMixingRecipe(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMixingRecipe(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        addMixingRecipe(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        addMixingRecipe(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        addMixingRecipe(Potions.POISON, Tags.Items.DUSTS_REDSTONE, Potions.LONG_POISON);
        addMixingRecipe(Potions.POISON, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_POISON);
        addMixingRecipe(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        addMixingRecipe(Potions.REGENERATION, Tags.Items.DUSTS_REDSTONE, Potions.LONG_REGENERATION);
        addMixingRecipe(Potions.REGENERATION, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_REGENERATION);
        addMixingRecipe(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        addMixingRecipe(Potions.STRENGTH, Tags.Items.DUSTS_REDSTONE, Potions.LONG_STRENGTH);
        addMixingRecipe(Potions.STRENGTH, Tags.Items.DUSTS_GLOWSTONE, Potions.STRONG_STRENGTH);
        addMixingRecipe(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        addMixingRecipe(Potions.WEAKNESS, Tags.Items.DUSTS_REDSTONE, Potions.LONG_WEAKNESS);
        addMixingRecipe(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        addMixingRecipe(Potions.SLOW_FALLING, Tags.Items.DUSTS_REDSTONE, Potions.LONG_SLOW_FALLING);

        for (Triple<Potion, Either<Item, Tag.Named<Item>>, Potion> mixingRecipe : mixingRecipes) {
            potions.add(mixingRecipe.getLeft());
            potions.add(mixingRecipe.getRight());
            for (Item container : containers) {
                BrewingRecipeBuilder.brewing(
                        Ingredient.of(PotionUtils.setPotion(new ItemStack(container), mixingRecipe.getLeft())),
                        mixingRecipe.getMiddle().map(Ingredient::of, Ingredient::of),
                        PotionUtils.setPotion(new ItemStack(container), mixingRecipe.getRight())
                ).save(consumer, createRecipeName(
                        container.getRegistryName(),
                        mixingRecipe.getLeft().getRegistryName(),
                        mixingRecipe.getMiddle().map(IForgeRegistryEntry::getRegistryName, Tag.Named::getName),
                        container.getRegistryName(),
                        mixingRecipe.getRight().getRegistryName()
                ));
            }
        }
        for (Triple<Item, Either<Item, Tag.Named<Item>>, Item> containerRecipe : containerRecipes) {
            for (Potion potion : potions) {
                BrewingRecipeBuilder.brewing(
                        Ingredient.of(PotionUtils.setPotion(new ItemStack(containerRecipe.getLeft()), potion)),
                        containerRecipe.getMiddle().map(Ingredient::of, Ingredient::of),
                        PotionUtils.setPotion(new ItemStack(containerRecipe.getRight()), potion)
                ).save(consumer, createRecipeName(
                        containerRecipe.getLeft().getRegistryName(),
                        potion.getRegistryName(),
                        containerRecipe.getMiddle().map(IForgeRegistryEntry::getRegistryName, Tag.Named::getName),
                        containerRecipe.getRight().getRegistryName(),
                        potion.getRegistryName()
                ));
            }
        }
    }

    private ResourceLocation createRecipeName(final ResourceLocation baseContainer, final ResourceLocation basePotion, final ResourceLocation reagent, final ResourceLocation resultContainer, final ResourceLocation resultPotion)
    {
        final String name = String.join("_",
                baseContainer.getPath(),
                basePotion.getPath(),
                "with",
                reagent.getPath().replace('/', '_'),
                "to",
                resultContainer.getPath(),
                resultPotion.getPath()
        );
        return new ResourceLocation(resultPotion.getNamespace(), "brewing/"+name);
    }

    private void addMixingRecipe(final Potion base, final Item reagent, final Potion result)
    {
        mixingRecipes.add(Triple.of(base, Either.left(reagent), result));
    }

    private void addMixingRecipe(final Potion base, final Tag.Named<Item> reagent, final Potion result)
    {
        mixingRecipes.add(Triple.of(base, Either.right(reagent), result));
    }

    private void addContainerRecipe(final Item base, final Item reagent, final Item result)
    {
        containerRecipes.add(Triple.of(base, Either.left(reagent), result));
    }

    @Override
    protected void saveAdvancement(HashCache cache, JsonObject advancementJson, Path pathIn) {
        if (pathIn.toString().contains("brewing"))
        {
            super.saveAdvancement(cache, advancementJson, pathIn);
        }
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
