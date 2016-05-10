package net.minecraftforge.test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.AnvilRecipeHandler;
import net.minecraftforge.common.IAnvilRecipe;
import net.minecraftforge.common.SimpleAnvilRecipe;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = AnvilRecipeTest.MODID, version = AnvilRecipeTest.VERSION)
public class AnvilRecipeTest
{
    public static final String MODID = "IAnvilRecipeTest";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        AnvilRecipeHandler.INSTANCE.add(new SimpleAnvilRecipe(new ItemStack(Items.experience_bottle), Items.glass_bottle, null, 0, 1));
        AnvilRecipeHandler.INSTANCE.add(new SimpleAnvilRecipe(new ItemStack(Items.iron_chestplate), Items.leather_chestplate, Items.iron_ingot, 5, 0));
        AnvilRecipeHandler.INSTANCE.add(new MelonAnvilRecipe());
        AnvilRecipeHandler.INSTANCE.add(new MegmaCreamAnvilRecipe());
    }

    public static class MelonAnvilRecipe implements IAnvilRecipe
    {
        protected final List<ItemStack> left;
        protected final List<ItemStack> right;

        public MelonAnvilRecipe()
        {
            this.left = Collections.singletonList(new ItemStack(Items.wooden_axe));
            this.right = Collections.singletonList(new ItemStack(Blocks.melon_block));
        }

        @Override
        public boolean matches(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            return inputLeft != null && inputRight != null && inputLeft.getItem() instanceof ItemAxe && inputRight.getItem() == Item.getItemFromBlock(Blocks.melon_block);
        }

        @Override
        public Pair<List<ItemStack>, List<ItemStack>> getInput()
        {
            return Pair.of(this.left, this.right);
        }

        @Override
        public int getMaterialCost()
        {
            return 1;
        }

        @Override
        public ItemStack getResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            return new ItemStack(Items.melon, 6);
        }

        @Override
        public int getExpCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            return 0;
        }

        @Override
        public void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            inputLeft.attemptDamageItem(1, new Random());
            inputRight.stackSize -= 1;
        }
    }

    public static class MegmaCreamAnvilRecipe extends SimpleAnvilRecipe
    {
        public MegmaCreamAnvilRecipe()
        {
            super(new ItemStack(Items.magma_cream), Items.lava_bucket, "slimeball", 1, 1);
        }

        @Override
        public ItemStack getResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            ItemStack result = this.output.copy();
            result.stackSize = inputRight.stackSize;
            return result;
        }

        @Override
        public int getExpCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            return inputRight == null ? 0 : this.expCost * ((inputRight.stackSize / 8) + 1);
        }

        @Override
        public void doRepair(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            inputLeft.setItem(Items.bucket);
            inputRight.stackSize = 0;
        }
    }
}
