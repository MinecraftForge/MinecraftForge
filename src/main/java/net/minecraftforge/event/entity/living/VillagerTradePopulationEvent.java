package net.minecraftforge.event.entity.living;

import java.util.List;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;

/**
 * The VillagerTradePopulationEvent is fired when the trades are populated<br>
 * in {@link EntityVillager#populateBuyingList()}.<br>
 * It allows the modder to modify the trades of an individual Villager.<br>
 * This event is fired via the {@link ForgeHooks#onVillagerTradePopulation}.<br>
 * {@link #villager} contains the villager the trades are applied to.<br>
 * {@link #career} contains the villager's careerr.<br>
 * {@link #trades} contains the trades the villager wil get<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * This event does not have a result. {@link HasResult}<br>
 * The modifications are made to the given list in {@link #trades}.
 *<br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class VillagerTradePopulationEvent extends Event
{

    private final EntityVillager villager;
    private final VillagerCareer career;
    private final List<ITradeList> trades;
    
    public VillagerTradePopulationEvent(EntityVillager villager, VillagerCareer career, List<ITradeList> originalTrades)
    {
        this.villager = villager;
        this.career = career;
        this.trades = originalTrades;
    }
    
    /**
     * @return the villager these trades are added to.
     */
    public EntityVillager getVillager()
    {
        return this.villager;
    }
    
    /**
     * @return the villager's career
     */
    public VillagerCareer getVillagerCareer()
    {
        return this.career;
    }
    
    /**
     * 
     * @return the list containing the villager trades.
     */
    public List<ITradeList> getTrades()
    {
        return this.trades;
    }
}
