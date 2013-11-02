package net.minecraftforge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IFurnaceRecipe;

public class StandardFurnaceRecipe implements IFurnaceRecipe {

    private final int itemId;
    private final float experience;
    private final ItemStack result;
    private final int smeltTime;
    
    public StandardFurnaceRecipe(int itemId, float experience, ItemStack result)
    {
        this(itemId, experience, result, 200);
    }
    
    public StandardFurnaceRecipe(int itemId, float experience, ItemStack result, int smeltTime)
    {
        this.itemId = itemId;
        this.experience = experience;
        this.result = result;
        this.smeltTime = smeltTime;
    }

    @Override
    public boolean matches(ItemStack smeltedItem)
    {
        return smeltedItem.itemID == itemId;
    }

    @Override
    public ItemStack getResult(ItemStack smeltedItem)
    {
        return result;
    }

    @Override
    public float getExperience(ItemStack smeltedItem, EntityPlayer player)
    {
        return experience;
    }

    @Override
    public int getSmeltTime(ItemStack smeltedItem)
    {
        return smeltTime;
    }
    
    public static class WithMeta extends StandardFurnaceRecipe {

        private final int meta;
        
        public WithMeta(int itemId, int meta, float experience, ItemStack result)
        {
            super(itemId, experience, result);
            this.meta = meta;
        }
        
        public WithMeta(int itemId, int meta, float experience, ItemStack result, int smeltTime)
        {
            super(itemId, experience, result, smeltTime);
            this.meta = meta;
        }

        @Override
        public boolean matches(ItemStack smeltedItem)
        {
            return super.matches(smeltedItem) && smeltedItem.getItemDamage() == meta;
        }
        
    }

}
