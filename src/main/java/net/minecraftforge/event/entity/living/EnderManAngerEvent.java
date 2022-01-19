package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Event that is fired when an enderman checks if the player is looking at him.
 * If cancelled the enderman does not get angry at the player.
 */
@Cancelable
public class EnderManAngerEvent extends LivingEvent {
    private final Player player;

    public EnderManAngerEvent(EnderMan enderman, Player player) {
        super(enderman);
        this.player = player;
    }

    /**
     * The player that is being checked.
     */
    public Player getPlayer() {
        return player;
    }
}
