package net.minecraftforge.client.model.armor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ArmorModelRegistry
{
    private static Map<Item, IModelProvider> bipedArmorModels = new ConcurrentHashMap<>();
    private static Map<Item, IModelProvider> bipedHorseArmorModels = new ConcurrentHashMap<>();
    private static final IModelProvider EMPTY = new IModelProvider() {};

    /**
     * Attaches a custom biped armor model to an item.
     * 
     * @param item The associated item
     * @param armorModelProvider The model provider of the armor
     * */
    public static <A extends BipedModel<?>> void attachBipedArmorModel(Item item, IModelProvider armorModelProvider)
    {
        bipedArmorModels.put(item, armorModelProvider);
    }

    /**
     * Gets the biped armor model of an item.
     * 
     * @param entityIn The entity wearing the item
     * @param stackIn The item to render the model of
     * @param armorSlotIn The slot the armor is in
     * @param _default The original model (will have attributes set)
     * @return A biped model to render, or null to use the default
     * */
    public static <A extends BipedModel<?>> A getBipedArmorModel(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, A _default)
    {
        return bipedArmorModels.getOrDefault(stackIn.getItem(), EMPTY).getModel(entityIn, stackIn, armorSlotIn, _default);
    }

    /**
     * Gets the texture location of the biped armor model.
     *
     * @param entityIn The entity wearing the item
     * @param stackIn The item to render the model of
     * @param armorSlotIn The slot the armor is in
     * @param textureTypeIn The texture subtype, can be null or "overlay"
     * @return The texture location to bind, or null to use the default
     * */
    public static ResourceLocation getBipedArmorTexture(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, String textureTypeIn)
    {
        return bipedArmorModels.getOrDefault(stackIn.getItem(), EMPTY).getTexture(entityIn, stackIn, armorSlotIn, textureTypeIn);
    }

    /**
     * Attaches a custom horse armor model to an item.
     * 
     * @param item The associated item
     * @param armorModelProvider The model provider of the armor
     * */
    public static <A extends HorseArmorModel<?>> void attachHorseArmorModel(Item item, IModelProvider armorModelProvider)
    {
    	bipedHorseArmorModels.put(item, armorModelProvider);
    }

    /**
     * Gets the horse armor model of an item.
     * 
     * @param entityIn The entity wearing the item
     * @param stackIn The item to render the model of
     * @param armorSlotIn The slot the armor is in
     * @param _default The original model (will have attributes set)
     * @return A horse model to render, or null to use the default
     * */
    public static <A extends HorseArmorModel<?>> A getHorseArmorModel(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, A _default)
    {
        return bipedHorseArmorModels.getOrDefault(stackIn.getItem(), EMPTY).getModel(entityIn, stackIn, armorSlotIn, _default);
    }

    /**
     * Gets the texture location of the horse armor model.
     *
     * @param entityIn The entity wearing the item
     * @param stackIn The item to render the model of
     * @param armorSlotIn The slot the armor is in
     * @param textureTypeIn The texture subtype, can be null or "overlay"
     * @return The texture location to bind, or null to use the default
     * */
    public static ResourceLocation getHorseArmorTexture(LivingEntity entityIn, ItemStack stackIn, EquipmentSlotType armorSlotIn, String textureTypeIn)
    {
        return bipedHorseArmorModels.getOrDefault(stackIn.getItem(), EMPTY).getTexture(entityIn, stackIn, armorSlotIn, textureTypeIn);
    }
}
