/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package net.minecraftforge.client.overlay;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Handler for the appearance of the effect overlays on armor models. {@link IArmorOverlayColor} is for the coloring.
 * */
public interface IArmorOverlayHandler
{

    IArmorOverlayHandler VANILLA = new Vanilla();

    /**
     * Vanilla's default Glint texture.
     * */
    ResourceLocation defaultGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    /**
     * Used for setting the effect texture at the start of a pass.
     *
     * @param stack the ItemStack in question
     * @param wearer the Entity in question
     *
     * @return the ResourceLocation of the effect texture.
     * */
    ResourceLocation getOverlayTexture(ItemStack stack, EntityLivingBase wearer);

    /**
     * Passes vectors for the scaling, rotation, and translation during effect rendering. <br>
     * <b>PLEASE NOTE: These arrays are static, and therefore contain values from the previous handler. You must reset all values or artifacts will be present</b>
     *
     * @param armorStack the ItemStack representing the armor
     * @param wearer the Entity wearing the armor
     * @param slot the Slot that the armor is in
     * @param time the timing of the effect
     * @param scaleVector a vec3 representing the scaling of the effect
     * @param rotationVector a vec4 representing the rotation of the effect
     * @param translationVector a vec3 representing the translation of the effect
     * */
    void manageFirstPassVectors(ItemStack armorStack, EntityLivingBase wearer, EntityEquipmentSlot slot, float time, final float[] scaleVector, final float[] rotationVector, final float[] translationVector);

    /**
     * Passes vectors for the scaling, rotation, and translation during effect rendering. <br>
     * <b>PLEASE NOTE: These arrays are static, and therefore contain values from the previous pass. You must reset all values or artifacts will be present</b>
     *
     * @param armorStack the ItemStack representing the armor
     * @param wearer the Entity wearing the armor
     * @param slot the Slot that the armor is in
     * @param time the timing of the effect
     * @param scaleVector a vec3 representing the scaling of the effect
     * @param rotationVector a vec4 representing the rotation of the effect
     * @param translationVector a vec3 representing the translation of the effect
     * */
    void manageSecondPassVectors(ItemStack armorStack, EntityLivingBase wearer,  EntityEquipmentSlot slot, float time, final float[] scaleVector, final float[] rotationVector, final float[] translationVector);

    class Vanilla implements IArmorOverlayHandler
    {

        protected Vanilla(){}

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack, EntityLivingBase wearer)
        {
            return defaultGlint;
        }

        @Override
        public void manageFirstPassVectors(ItemStack armorStack, EntityLivingBase wearer,  EntityEquipmentSlot slot, float time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            scaleVector[0] = 0.33333334F;
            scaleVector[1] = 0.33333334F;
            scaleVector[2] = 0.33333334F;

            rotationVector[0] = 30F;
            rotationVector[1] = 0F;
            rotationVector[2] = 0F;
            rotationVector[3] = 1F;

            translationVector[0] = 0F;
            translationVector[1] = 0.0006F * time;
            translationVector[2] = 0F;
        }

        @Override
        public void manageSecondPassVectors(ItemStack armorStack, EntityLivingBase wearer,  EntityEquipmentSlot slot, float time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            rotationVector[0] = -30F;

            translationVector[1] = time * 0.06006F;
        }

    }

}