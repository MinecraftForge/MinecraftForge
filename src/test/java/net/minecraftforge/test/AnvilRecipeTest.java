package net.minecraftforge.test;

import java.util.List;
import java.util.Random;
import com.google.common.collect.ImmutableList;
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
        AnvilRecipeHandler.INSTANCE.addRepair(new SimpleAnvilRecipe(new ItemStack(Items.iron_chestplate), Items.leather_chestplate, Items.iron_ingot, 5, 0));
        AnvilRecipeHandler.INSTANCE.addRepair(new MelonAnvilRecipe());
        AnvilRecipeHandler.INSTANCE.addRepair(new MegmaCreamAnvilRecipe());
    }

    public static class MelonAnvilRecipe implements IAnvilRecipe
    {
        protected final List<ItemStack> left;
        protected final List<ItemStack> right;

        public MelonAnvilRecipe()
        {
            this.left = ImmutableList.of(new ItemStack(Items.wooden_axe));
            this.right = ImmutableList.of(new ItemStack(Blocks.melon_block));
        }

        @Override
        public boolean matches(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            return inputLeft != null && inputRight != null && inputLeft.getItem() instanceof ItemAxe && inputRight.getItem() == Item.getItemFromBlock(Blocks.melon_block);
        }

        @Override
        public List[] getInputs()
        {
            return new List[] { this.left, this.right };
        }

        @Override
        public ItemStack getResult(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
        {
            return new ItemStack(Items.melon, 6);
        }

        @Override
        public int getCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
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
        public int getCost(ItemStack inputLeft, ItemStack inputRight, String newName, World world)
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
