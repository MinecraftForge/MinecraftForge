package net.minecraftforge.client.model.armor;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * An interface to hold a custom model and texture 
 * associated with the current item worn. Used to 
 * add armor to biped and horse models.
 */
@FunctionalInterface
public interface IModelProvider<M extends EntityModel<?>>
{
	/**
     * Gets the associated model of an entity or the default if null.
     *
     * @param entityIn The entity wearing the item
     * @param stackIn The item to render the model of
     * @param armorSlotIn The slot the armor is in
     * @param _default The original model (will have attributes set)
     * @return An entity model to render, or null to use the default
     */
	@Nullable <A extends M> A getModel(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, A _default);

    /**
     * Gets the texture location of the associated model or the default if null.
     *
     * @param entityIn The entity wearing the item
     * @param stackIn The item to render the model of
     * @param armorSlotIn The slot the armor is in
     * @param textureTypeIn The texture subtype, can be null or "overlay"
     * @return The texture location to bind, or null to use the default
     */
    @Nullable
    default ResourceLocation getTexture(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, @Nullable String textureTypeIn)
    {
        return null;
    }
}
