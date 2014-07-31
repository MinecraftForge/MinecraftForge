package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * When the player receives an achievement. If canceled the player will not receive anything.
 */
@Cancelable
public class AchievementEvent extends PlayerEvent {

    public final Achievement achievement;
    public final boolean playerHasAchievement;
    
    public AchievementEvent(EntityPlayerMP player, Achievement achievement)
    {
        super(player);
        this.achievement = achievement;
        this.playerHasAchievement = player.func_147099_x().hasAchievementUnlocked(achievement);
    }
}
