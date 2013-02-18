package net.minecraftforge.common;

import net.minecraft.client.model.ModelBiped;
/**
 * This interface has to be implemented by an instance of ItemArmor.
 * It allows for the application of a custom model file to the player skin
 * when the armor is worn.
 */
public interface IArmorModelProvider
{
    /**
     * This interface returns an instance of a model to render.
     * Logic such as which parts to show are up to the implementation.
     */
    public ModelBiped provideArmorModel(int armorSlot);
}
