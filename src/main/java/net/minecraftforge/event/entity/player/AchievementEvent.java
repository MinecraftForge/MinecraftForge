package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * When the player receives an achievement. If canceled the player will not receive anything.
 */
@Cancelable
public class AchievementEvent extends PlayerEvent {

    public final Achievement achievement;
    public AchievementEvent(EntityPlayer player, Achievement achievement)
    {
        super(player);
        this.achievement = achievement;
    }
}
