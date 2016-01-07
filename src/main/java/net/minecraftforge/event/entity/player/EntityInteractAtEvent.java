package net.minecraftforge.event.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * EntityInteractAtEvent is fired when a player interacts with an Entity<br>
 * at a specific position (Vec3 hitVec from MovingObjectPosition).<br>
 * This event is fired whenever a player interacts with an Entity<br>
 * in Minecraft#rightClickMouse() and on the server in<br>
 * NetHandlerPlayServer#processUseEntity().<br>
 * <br>
 * {@link #target} contains the Entity the player interacted with.<br>
 * {@link #hitVec} contains the interaction point on the Entity.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the player does not interact with the Entity
 * via the Entity#interactAt() method.<br>
 * Note that the EntityInteractEvent will then fire instead.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class EntityInteractAtEvent extends PlayerEvent
{
    public final Entity target;
    public final Vec3 hitVec;

    public EntityInteractAtEvent(EntityPlayer player, Entity target, Vec3 vec3)
    {
        super(player);
        this.target = target;
        this.hitVec = vec3;
    }
}
