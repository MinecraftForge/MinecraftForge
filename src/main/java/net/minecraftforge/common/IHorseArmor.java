package net.minecraftforge.common;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IHorseArmor 
{
    /** 
     * Returns an enum constant of type {@code HorseArmorType}.
     * The returned enum constant will be used to determine the armor value and texture of this item when equipped.
     * @param stack the armor stack
     * @return an enum constant of type {@code HorseArmorType}
     */
    HorseArmorType getHorseArmorType(ItemStack stack);
    
    /**
     * Called every tick from {@link EntityHorse#onUpdate()} on the item in the armor slot.
     * @param world the world the horse is in
     * @param horse the horse wearing this armor
     * @param armor the armor itemstack
     */
    default void onHorseArmorTick(World world, EntityHorse horse, ItemStack armor) {}
}
