package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * TradeWithVillagerEvent is fired when a player trades with
 * a villager inheriting from {@link AbstractVillager}. <br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 */
public class TradeWithVillagerEvent extends PlayerEvent
{
    private final MerchantOffer offer;
    private final AbstractVillager abstractVillager;

    public TradeWithVillagerEvent(Player player, MerchantOffer offer, AbstractVillager abstractVillager)
    {
        super(player);
        this.offer = offer;
        this.abstractVillager = abstractVillager;
    }

    /**
     * The {@link MerchantOffer} the player used to trade with the villager.
     */
    public MerchantOffer getMerchantOffer()
    {
        return offer;
    }

    /**
     * The {@link AbstractVillager} used to complete the trade with.
     */
    public AbstractVillager getAbstractVillager()
    {
        return abstractVillager;
    }
}