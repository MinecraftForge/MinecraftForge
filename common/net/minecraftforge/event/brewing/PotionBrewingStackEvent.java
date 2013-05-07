package net.minecraftforge.event.brewing;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.Event.HasResult;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class PotionBrewingStackEvent extends Event {

    public final ItemStack brewingStack;
    public final ItemStack ingredient;

    public PotionBrewingStackEvent(ItemStack brewingStack, ItemStack ingredient)
    {
        this.brewingStack = brewingStack;
        this.ingredient = ingredient;
    }

    @Cancelable
    public static class BrewingCraftEvent extends PotionBrewingStackEvent
    {
        /**
         * Called once for each brewingStack before the Potion Ingredient is
         * applied to the Potion being brewed
         * 
         * @param brewingStack
         *            Itemstack representing the Potion to be brewed. Is
         *            instance of ItemPotion and not null.
         * @param ingredient
         *            Itemstack representing ingredient to be brewed with
         *            brewingStack.
         */
        public BrewingCraftEvent(ItemStack brewingStack, ItemStack ingredient) { super(brewingStack, ingredient); }
    }
    
    @HasResult
    public static class CanBrewEvent extends PotionBrewingStackEvent
    {
        /**
         * Called once for each brewingStack before the Potion being brewed is
         * tested to see if the Potion Ingredient is applicable
         * 
         * @param brewingStack
         *            Itemstack representing the Potion to be brewed. Is
         *            instance of ItemPotion and not null.
         * @param ingredient
         *            Itemstack representing ingredient to be brewed with
         *            brewingStack.
         */
        public CanBrewEvent(ItemStack brewingStack, ItemStack ingredient) { super(brewingStack, ingredient); }
    }
}
