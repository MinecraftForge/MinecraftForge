package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingEatEvent is fire when an LivingEntity eats food (item, cake, etc.).<br>
 * Specifically it is fired for the method call {@link FoodData#eat()}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingEat()} .<br>
 * <br>
 * {@link #foodAmount} contains the amount of food gain to occur<br>
 * {@link #saturationAmount} contains the amount of saturation gain to occur<br>
 * {@link #foodItem} contains the {@link ItemStack} that the entity ate (if available) for reference<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the FoodData does not change.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingEatEvent extends LivingEvent
{
	private int foodAmount;
	private float saturaionAmount;
	private ItemStack foodItem = null;
	
	public LivingEatEvent(@javax.annotation.Nullable LivingEntity entity, int foodAmount, float saturationAmount) {
		super(entity);
		this.foodAmount = foodAmount;
		this.saturaionAmount = saturationAmount;
	}
	
	public LivingEatEvent(@javax.annotation.Nullable LivingEntity entity, ItemStack foodItem, FoodProperties prop) {
		this(entity, prop.getNutrition(), prop.getSaturationModifier());
		this.foodItem = foodItem;
	}

	public int getFoodAmount() {
		return foodAmount;
	}

	public void setFoodAmount(int foodAmount) {
		this.foodAmount = foodAmount;
	}

	public float getSaturaionAmount() {
		return saturaionAmount;
	}

	public void setSaturaionAmount(float saturaionAmount) {
		this.saturaionAmount = saturaionAmount;
	}

	public ItemStack getFoodItem() {
		return foodItem;
	}
	
}
