package net.minecraftforge.event.entity.living;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * LivingFoodEffectEvent is fire when an LivingEntity eats food item.<br>
 * Specifically it is fired for the method call {@link LivingEntity#addEatEffect()}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingFoodEffect()} .<br>
 * <br>
 * {@link #foodItem} contains the {@link ItemStack} that the entity ate (if available) for reference<br>
 * {@link effectPairs} contains the effect-probability pair of {@link #foodItem} which can be modified 
 * to change the applied effects and probability<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, no effect will be applied.<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class LivingFoodEffectEvent extends LivingEvent {

	private final ItemStack foodItem;
	private List<Pair<MobEffectInstance, Float>> effectPairs;
	
	public LivingFoodEffectEvent(LivingEntity entity, ItemStack foodItem) {
		super(entity);
		this.foodItem = foodItem;
		effectPairs = foodItem.getFoodProperties(entity).getEffects();
	}

	public ItemStack getFoodItem() {
		return foodItem;
	}

	public List<Pair<MobEffectInstance, Float>> getEffects() {
		return effectPairs;
	}

}
