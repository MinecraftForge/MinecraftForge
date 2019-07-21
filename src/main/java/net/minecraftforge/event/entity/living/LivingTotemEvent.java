package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;

/**
 * LivingTotemEvent is fired when a player is about to die.
 * <br>
 * This event is fired whenever a player is about to die.
 * <br>
 * This event isn't {@link Cancelable}
 * <br>
 * This event has a result. {@link HasResult}
 * <br>
 * If you pass Result.Allow, you need to do any resulting handling such as setting effects and such.
 * Result.Allow does only handle Life-Setting and Entity State handling by default.
 * This is done to allow mod-authors to create their own "set of effects" for their own totems.
 * <br>
 *  Result.Allow = Protects the player, setting their health to the value set in getHealth or 1F.
 *  Result.Default = Runs the default Totem of Undying checks and balances.
 *  Result.Deny = Do not protect the player regardless of them holding the Totem.
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
@Event.HasResult
public class LivingTotemEvent extends LivingEvent
{
    /**
     * If the event returns Result.Allow, the value of health will be used to decide the health the player is set to when "protected".
     * By default it's not set and thus returns 0.
     * This is checked against in {@link ForgeEventFactory#checkTotemProtection} and returns a value of 1 if a value is not set.
     */
    private float health;
    private final DamageSource source;

    public LivingTotemEvent(LivingEntity livingBase, DamageSource source)
    {
        super(livingBase);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }

    public float getHealth()
    {
        return health;
    }

    public void setHealth(float health)
    {
        if (health > getEntityLiving().getMaxHealth())
        {
            this.health = getEntityLiving().getMaxHealth();
        }
        else
        {
            this.health = health;
        }
    }
}
