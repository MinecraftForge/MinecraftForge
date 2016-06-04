package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * LivingPotionEvent is fired when an EntityLivingBase has a change in potion effects.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #effect} contains the PotionEffect that is changing.<br>
 * {@link #type} contains the {@link LivingPotionEventType} for this event.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS} on the server.<br>
 **/
public class LivingPotionEvent extends LivingEvent {
    protected PotionEffect effect;
    private final LivingPotionEventType type;

    public LivingPotionEvent(EntityLivingBase entity, PotionEffect effect, LivingPotionEventType type) {
        super(entity);
        this.effect = effect;
        this.type = type;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    public LivingPotionEventType getType() {
        return type;
    }

    /**
     * LivingPotionAddedEvent is fired when an EntityLivingBase is about to receive a new potion effect.<br>
     * This event is fired whenever an EntityLivingBase has a potion added through the
     * {@link EntityLivingBase#addPotionEffect(PotionEffect)} method.<br>
     * <br>
     * This event is fired via {@link ForgeHooks#onLivingPotionAdded(EntityLivingBase, PotionEffect)}.<br>
     * <br>
     * {@link #entity} contains the EntityLivingBase that will have the new potion effect.<br>
     * {@link #effect} contains the PotionEffect about to be added.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the Entity does not get the new potion effect.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class LivingPotionAddedEvent extends LivingPotionEvent {
        public LivingPotionAddedEvent(EntityLivingBase entity, PotionEffect effect) {
            super(entity, effect, LivingPotionEventType.ADDED);
        }
    }

    /**
     * LivingPotionCuredEvent is fired when an EntityLivingBase is about to cure a potion effect.<br>
     * This event is fired whenever an EntityLivingBase attempts to cure a potion through the
     * {@link EntityLivingBase#curePotionEffects(ItemStack)} method.<br>
     * <br>
     * This event is fired via {@link ForgeHooks#onLivingPotionCured(EntityLivingBase, PotionEffect, ItemStack)}.<br>
     * <br>
     * {@link #entity} contains the EntityLivingBase that will have the effect cured.<br>
     * {@link #effect} contains the PotionEffect about to be cured.<br>
     * {@link #curativeItem} contains the ItemStack used to cure the effect.<br>
     * <br>
     * This event is {@link Cancelable}.<br>
     * If this event is canceled, the Entity does not cure this potion effect with the item.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class LivingPotionCuredEvent extends LivingPotionEvent {
        private final ItemStack curativeItem;

        public LivingPotionCuredEvent(EntityLivingBase entity, PotionEffect effect, ItemStack curativeItem) {
            super(entity, effect, LivingPotionEventType.CURED);
            this.curativeItem = curativeItem;
        }

        public ItemStack getCurativeItem() {
            return curativeItem;
        }
    }

    /**
     * LivingPotionExpiredEvent is fired when an EntityLivingBase is about to have a potion effect expire.<br>
     * This event is fired whenever an EntityLivingBase a potion effect would expire in the
     * {@link EntityLivingBase#updatePotionEffects()} method.<br>
     * <br>
     * This event is fired via {@link ForgeHooks#onLivingPotionExpired(EntityLivingBase, PotionEffect)}.<br>
     * <br>
     * {@link #entity} contains the EntityLivingBase that will have the effect expire.<br>
     * {@link #effect} contains the PotionEffect about to be expired.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class LivingPotionExpiredEvent extends LivingPotionEvent {
        public LivingPotionExpiredEvent(EntityLivingBase entity, PotionEffect effect) {
            super(entity, effect, LivingPotionEventType.EXPIRED);
        }
    }


    /**
     * LivingPotionRemovedEvent is fired when an EntityLivingBase is about to have a potion effect removed.<br>
     * This event is fired whenever an EntityLivingBase a potion effect would be removed in the
     * {@link EntityLivingBase#clearActivePotions()} and {@link EntityLivingBase#removeActivePotionEffect(Potion)} methods.<br>
     * <br>
     * This event is fired via {@link ForgeHooks#onLivingPotionRemoved(EntityLivingBase, PotionEffect, boolean)}.<br>
     * <br>
     * {@link #entity} contains the EntityLivingBase that will have the effect removed.<br>
     * {@link #effect} contains the PotionEffect about to be removed.<br>
     * {@link #byCommand} contains if the effect was removed via admin command or not.
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class LivingPotionRemovedEvent extends LivingPotionEvent {
        private final boolean byCommand;

        public LivingPotionRemovedEvent(EntityLivingBase entity, PotionEffect effect, boolean byCommand) {
            super(entity, effect, LivingPotionEventType.REMOVED);
            this.byCommand = byCommand;
        }

        public boolean isByCommand() {
            return byCommand;
        }
    }

    public static enum LivingPotionEventType {
        ADDED, EXPIRED, CURED, REMOVED
    }
}
