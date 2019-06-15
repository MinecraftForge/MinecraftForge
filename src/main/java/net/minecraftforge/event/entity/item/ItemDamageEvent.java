package net.minecraftforge.event.entity.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Event that is fired when an EntityItem is damaged from an outside source.
 * This event is called after the EntityItem is checked for invulnerability to the DamageSource.<br>
 * <br>
 * {@link #source} contains the {@link DamageSource} that caused this entity to be damaged.<br>
 * {@link #amount} contains the final amount of damage that will be dealt to the Entity.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the EntityItem is not damaged.
 */
@Cancelable
public class ItemDamageEvent extends ItemEvent
{
	private DamageSource source;
	private float amount;

	/**
	 * Creates a new event for an EntityItem that is taking damage.
	 *
	 * @param entityItem The EntityItem being damaged.
	 * @param source The {@link DamageSource} source of the damage.
	 * @param amount The amount of damage being dealt.
	 */
	public ItemDamageEvent(EntityItem entityItem, DamageSource source, float amount)
	{
		super(entityItem);
		this.source = source;
		this.amount = amount;
	}

	public DamageSource getSource()
	{
		return source;
	}

	public float getAmount()
	{
		return amount;
	}

	public void setAmount(float amount)
	{
		this.amount = amount;
	}
}
