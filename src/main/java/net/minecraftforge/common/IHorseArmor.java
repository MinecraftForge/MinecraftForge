package net.minecraftforge.common;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IHorseArmor 
{
    /** 
     * @param stack - The armor stack
     * @return The HorseArmorType of the armor stack 
     */
    public HorseArmorType getHorseArmorType(ItemStack stack);
    
    /**
     * @param world - The world the horse is in
     * @param horse - The horse wearing the armor
     * @param itemStack - the armor itemstack
     */
    public void onHorseArmorTick(World world, EntityHorse horse, ItemStack itemStack);
}
