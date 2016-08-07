package net.minecraftforge.ingredients.capability.wrappers;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.ingredients.*;

import javax.annotation.Nullable;

public final class VanillaIngredientCapabilityInjector
{
    public static final ResourceLocation ResourceHandle = new ResourceLocation(ForgeModContainer.getInstance().getModId(), "ingredient");

    private static final Item ironOre;
    private static final Item ironBlock;
    private static final Item goldOre;
    private static final Item goldBlock;
    private static final Item diamondOre;
    private static final Item diamondBlock;

    static
    {
        BiMap<Block, Item> map = GameData.getBlockItemMap();
        ironOre = map.get(Blocks.IRON_ORE);
        ironBlock = map.get(Blocks.IRON_BLOCK);
        goldOre = map.get(Blocks.GOLD_ORE);
        goldBlock = map.get(Blocks.GOLD_BLOCK);
        diamondBlock = map.get(Blocks.DIAMOND_BLOCK);
        diamondOre = map.get(Blocks.DIAMOND_ORE);
    }

    private VanillaIngredientCapabilityInjector()
    {
    }

    /**
     * Injects vanilla Items with the necessary ingredient capability.
     * <p/>
     * Necessary because many needed items do not have their own unique class.
     * */
    @Nullable
    public static ICapabilityProvider getWrapperForItem(Item item, ItemStack stack)
    {
        if(item == Items.DIAMOND)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.DIAMOND, Ingredient.ORE_VOLUME, EnumRefinementLevel.REFINED));
        }
        if(item == Items.IRON_INGOT)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.IRON, Ingredient.ORE_VOLUME));
        }
        if(item == Items.GOLD_INGOT)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.GOLD, Ingredient.ORE_VOLUME));
        }
        if(item == Items.GOLD_NUGGET)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.GOLD, Ingredient.NUGGET_VOLUME));
        }
        if(item == ironOre)
        {
            return new WrapperOreBlock(stack, IngredientRegistry.IRON, IngredientRegistry.STONE);
        }
        if(item == ironBlock)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.IRON, Ingredient.FULL_BLOCK_VOLUME, EnumRefinementLevel.REFINED));
        }
        if(item == goldOre)
        {
            return new WrapperOreBlock(stack, IngredientRegistry.GOLD, IngredientRegistry.STONE);
        }
        if(item == goldBlock)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.GOLD, Ingredient.FULL_BLOCK_VOLUME, EnumRefinementLevel.REFINED));
        }
        if(item == diamondOre)
        {
            return new WrapperOreBlock(stack, IngredientRegistry.DIAMOND, IngredientRegistry.STONE);
        }
        if(item == diamondBlock)
        {
            return new WrapperPureItem(stack, new IngredientStack(IngredientRegistry.DIAMOND, Ingredient.FULL_BLOCK_VOLUME, EnumRefinementLevel.REFINED));
        }
        return null;
    }
}
