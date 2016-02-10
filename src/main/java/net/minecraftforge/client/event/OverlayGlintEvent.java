package net.minecraftforge.client.event;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
/**
 * Events for hooking into the Glint effect Rendering<br>
 * Will allow for adding effects to items, creating custom overlays, modifying vanilla
 * colors, and cancelling of glint overlays. 
 * @author RANKSHANK
 */
@SideOnly(Side.CLIENT)
public class OverlayGlintEvent<T> extends Event
{

    /**
     * The model used for the particular glint render.<br><br>
     * {@link IBakedModel} during {@link StackOverlayEvent}<br>
     * {@link ModelBase} during {@link ArmorOverlayEvent}<br>
     * */
    public final T theModel;

    /**
     * The ItemStack in question
     * */
    public final ItemStack theStack;

    /**
     * Change this to override the glint Color. Overrides the Item's default return value.<br>
     * May be useful for overriding Items without substitution<br><br>
     * <b>Please note:</b> You must pass an alpha byte as well. The values are ordered A R G B<br> 
     * You can add an alpha channel to colors using (0xFF << 24 | int) for full opacity
     * */
    public int glintValue;

    public OverlayGlintEvent(ItemStack stack, T model, int color)
    {
        theStack = stack;
        glintValue = color;
        theModel = model;
    }

    /**
     * Fired before the glint effect is rendered over the armor model.<br>
     * 
     * */
    @Cancelable
    public static class ArmorOverlayEvent extends OverlayGlintEvent<ModelBase>
    {

        /**
         * The slot that the armor is in
         * */
        public final int armorSlot;
    
        /**
         * The entity wearing the armor
         * */
        public final EntityLivingBase theEntity;

        public ArmorOverlayEvent(ItemStack stack, ModelBase model, EntityLivingBase entity, int slot)
        {
            super(stack, model, stack.getItem().getArmorGlintColor(stack, entity, slot));
            armorSlot = slot;
            theEntity = entity;
        }

    }

    /**
     * Fired before glint effect is rendered over the ItemStack.<br>
     * Can be cancelled to prevent the vanilla glint from rendering
     * */
    @Cancelable
    public static class StackOverlayEvent extends OverlayGlintEvent<IBakedModel>
    {

        public StackOverlayEvent(ItemStack stack, IBakedModel model)
        {
			super(stack, model, stack.getItem().getItemGlintColor(stack));
        }

    }

}
