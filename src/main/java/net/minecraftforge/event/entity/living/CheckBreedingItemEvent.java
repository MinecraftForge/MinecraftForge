package net.minecraftforge.event.entity.living;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * CheckBreedingItemEvent is fired when an EntityAnimal is right-clicked with a potential breeding item. <br>
 * This event is fired whenever an EntityAnimal is right-clicked by a player in 
 * EntityAnimal#interact(EntityPlayer).<br>
 * <br>
 * This event is fired via the {@link ForgeEventFactory#isBreedingItem(EntityAnimal, ItemStack, boolean)}.<br>
 * <br>
 * {@link #isBreedingItem} contains the boolean for whether the item in {@link #stack} is a valid breeding item. <br>
 * <br>
 * This event does not get called if {@link #stack} is null.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event will not make the entity tempt {@link #stack}.
 * You could do that by adding the {@link EntityAITempt} AI task whenever an entity of the desired type joins the world. {@link EntityJoinWorldEvent}<br>  
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class CheckBreedingItemEvent extends LivingEvent
{
    public boolean isBreedingItem;
    public final ItemStack stack;
    
    public CheckBreedingItemEvent(EntityAnimal entity, ItemStack stack, boolean isBreedingItem)
    {
        super(entity);
        this.stack = stack;
        this.isBreedingItem = isBreedingItem;
    }
}