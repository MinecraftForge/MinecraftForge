package net.minecraftforge.client.event;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.pipeline.EffectPassHandler;
import net.minecraftforge.client.model.pipeline.EffectPassHandler.ArmorEffectPassHandler;
import net.minecraftforge.client.model.pipeline.EffectPassHandler.ItemStackEffectPassHandler;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
/**
 * Events pertaining to the effect overlays on ItemStacks & armor models
 * */
@SuppressWarnings("rawtypes")
public abstract class EffectOverlayEvent<T,TT extends EffectPassHandler> extends Event
{

    protected  TT passHandler;

    private final ItemStack theStack;

    private int[] override;

    private EffectOverlayEvent(ItemStack stack, TT defaultPassHandler)
    {
        passHandler = defaultPassHandler;
        theStack = stack;
    }

    /**
     * @return The ItemStack being rendered
     * */
    public ItemStack getStack()
    {
        return theStack;
    }

    /**
     * Use this to set the glint Color.<br><br>
     * @param colorARGB the glint color array used for pass colors. Requires an alpha byte. Value is ordered A R G B
     * */
    public void setGlintColor(int... colorARGB)
    {
        override = colorARGB;
    }

    /**
     * @return The hex int value of the effect color ordered as A R G B 
     * */
    public int[] getOverriddenColor()
    {
        return override;
    }

    /**
     * @return The EffectPassHandler for the effect overlay
     */
    public TT getPassHandler()
    {
        return passHandler;
    }

    /**
     * Replaces the respective vanilla EffectPassHander with a new one.<br>
     * Passing null will cancel the effect pass.
     * @param effectPassHandler The new EffectPassHandler
     * */
    public void setPassHandler(TT effectPassHandler)
    {
        passHandler = effectPassHandler;
    }

    /**
     * Fired before the glint effect is rendered over the armor model.<br>
     * Cancel to prevent the vanilla glint from rendering.
     * */
    @Cancelable
    public static class ArmorEffectOverlayEvent extends EffectOverlayEvent<ModelBase, ArmorEffectPassHandler>
    {

        private final int armorSlot;

        private final EntityLivingBase theWearer;

        public ArmorEffectOverlayEvent(ItemStack stack, EntityLivingBase entity, int slot)
        {
            super(stack, EffectPassHandler.vanillaArmorPassHandler);
            armorSlot = slot;
            theWearer = entity;
        }

        /**
         * @return The id of the slot that the armor is in
         * */
        public int getArmorSlotID()
        {
            return armorSlot;
        }
        
        /**
         * @return The entity wearing the armor
         * */
        public final EntityLivingBase getArmorWearer()
        {
            return theWearer;
        }

    }

    /**
     * Fired before glint effect is rendered over the ItemStack.<br>
     * Cancel to prevent the vanilla glint from rendering.
     * */
    @Cancelable
    public static class ItemStackEffectOverlayEvent extends EffectOverlayEvent<IBakedModel, ItemStackEffectPassHandler>
    {

        public ItemStackEffectOverlayEvent(ItemStack stack)
        {
            super(stack, ItemStackEffectPassHandler.vanillaStackPassHandler);
        }

    }

}
