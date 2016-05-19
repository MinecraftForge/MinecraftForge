package net.minecraftforge.event.entity.living;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;

/**
 * VillagerTradePopulationEvent is fired when a villager gets his trade options.<br>
 * This event is fired at the end of {@link EntityVillager#populateBuyingList()}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onVillagerTradePopulation(EntityVillager, MerchantRecipeList, int)}.<br>
 * <br>
 * {@link LivingEntity#entityLiving} contains the villager.<br>
 * {@link #carer} contains the villager's career.<br>
 * {@link #tradeList} contains the villager's trade list.
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class VillagerTradePopulationEvent extends LivingEvent
{

    private final VillagerCareer career;
    private final MerchantRecipeList tradeList;

    public VillagerTradePopulationEvent(EntityVillager villager, VillagerCareer career, MerchantRecipeList tradeList)
    {
        super(villager);
        this.career = career;
        this.tradeList = tradeList;
    }

    /**
     * @return The villager's career.
     */
    public VillagerCareer getVillagerCareer()
    {
        return career;
    }

    /**
     * The returned list can be edited.
     * @return The list of trades the villager got so far.
     */
    public MerchantRecipeList getTradeList()
    {
        return tradeList;
    }
}
