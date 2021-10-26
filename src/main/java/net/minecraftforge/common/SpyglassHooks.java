package net.minecraftforge.common;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SpyglassHooks
{

    private static final List<Predicate<Player>> SPYGLASS_PREDICATES = new ArrayList<>();

    /**
     * Registers a predicate which will be checked for whether the player is considered to be 'scoping' with a spyglass on the client.
     * Only one registered predicate needs to be true for scoping to occur.
     * @param spyglassCondition A predicate which will be checked on the client side. Might be tested very frequently.
     */
    public static synchronized void registerSpyglassCondition(Predicate<Player> spyglassCondition)
    {
        SPYGLASS_PREDICATES.add(spyglassCondition);
    }

    public static boolean isPlayerScoping(Player player)
    {
        for(Predicate<Player> spyglassPredicate : SPYGLASS_PREDICATES)
        {
            if(spyglassPredicate.test(player))
            {
                return true;
            }
        }
        return false;
    }
}
