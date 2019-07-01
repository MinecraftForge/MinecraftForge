package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * LivingTotemEvent is fired when a player is about to die.
 * <br>
 * This event is fired whenever a player is about to die.
 * <br>
 * This event isn't {@link Cancelable}
 * <br>
 * This event has a result. {@link HasResult}
 *  Result.Allow = Respawns the Player regardless of them having a Totem.
 *  Result.Default = Runs the default Totem of Undying checks and balances.
 *  Result.Deny = Don't save the player regardless of them holding the Totem.
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
@Event.HasResult
public class LivingTotemEvent extends LivingEvent
{
    private final DamageSource source;

    public LivingTotemEvent(EntityLivingBase livingBase, DamageSource source)
    {
        super(livingBase);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }

}
