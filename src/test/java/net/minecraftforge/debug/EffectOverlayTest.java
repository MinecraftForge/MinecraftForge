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

package net.minecraftforge.debug;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.overlay.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid="effectoverlaytest", clientSideOnly = true)
public class EffectOverlayTest
{

    private static final ResourceLocation EMBER = new ResourceLocation("effectoverlaytest", "textures/misc/embers.png");

    @EventHandler
    public void peri(FMLInitializationEvent event)
    {
        Items.LEATHER_CHESTPLATE.subscribeStackOverlayHandler(testStackHandler);
        Items.LEATHER_CHESTPLATE.subscribeStackOverlayColor(testStackColor);
        Items.LEATHER_CHESTPLATE.subscribeArmorOverlayColor(testArmorColor);
        Items.LEATHER_CHESTPLATE.subscribeArmorOverlayHandler(testArmorHandler);

        Items.LEATHER_BOOTS.subscribeArmorOverlayColor(testArmorColor);
        Items.LEATHER_BOOTS.subscribeStackOverlayColor(testStackColor);

        Items.LEATHER_HELMET.subscribeArmorOverlayHandler(testArmorHandler);
        Items.LEATHER_HELMET.subscribeStackOverlayHandler(testStackHandler);
    }

    private static IStackOverlayColor testStackColor = new IStackOverlayColor() {

        @Override
        public int getFirstPassColor(ItemStack stack) {
            return 0xFF113311;
        }

        @Override
        public int getSecondPassColor(ItemStack stack) {
            return 0xFFCC1100;
        }

        @Override
        public boolean useForStack(ItemStack stack)
        {
            return true;
        }
    };

    private static final IStackOverlayHandler testStackHandler = new IStackOverlayHandler()
    {

        @Override
        public void manageFirstPassVectors(ItemStack stack, long time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            float scale = (float) Math.sin(time / 150000.0F);
            scaleVector[0] = scale;
            scaleVector[1] = scale;
            scaleVector[2] = scale;
            rotationVector[0] = 150.0F;
            rotationVector[1] = 0.0F;
            rotationVector[2] = 0.0F;
            rotationVector[3] = 1.0F;
            translationVector[0] = 1.0F;
            translationVector[1] = 0.0F;
            translationVector[2] = 0.0F;
        }

        @Override
        public void manageSecondPassVectors(ItemStack stack, long time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            float scale = (float) Math.cos(time / -177984.0F);
            scaleVector[0] = scale;
            scaleVector[1] = scale;
            rotationVector[0] = 190.0F;
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack)
        {
            return EMBER;
        }

        @Override
        public boolean useForStack(ItemStack stack)
        {
            return true;
        }

    };

    private static final IArmorOverlayColor testArmorColor = new IArmorOverlayColor()
    {

        @Override
        public int getFirstPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return 0xFF119999;
        }

        @Override
        public int getSecondPassColor(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return 0xCCFF0000;
        }

        @Override
        public boolean useForStack(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return true;
        }
    };

    private static final IArmorOverlayHandler testArmorHandler = new IArmorOverlayHandler()
    {

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack,EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return EMBER;
        }

        @Override
        public void manageFirstPassVectors(ItemStack armorStack, EntityLivingBase wearer, EntityEquipmentSlot slot, float time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            scaleVector[0] = 0.33333334F;
            scaleVector[1] = 0.33333334F;
            scaleVector[2] = 0.33333334F;
            rotationVector[0] = 120F;
            rotationVector[1] = 0.0F;
            rotationVector[2] = 0.0F;
            rotationVector[3] = 1.0F;
            translationVector[0] = time * -0.0033F;
            translationVector[1] = 0.0F;
            translationVector[2] = 0.0F;
        }

        @Override
        public void manageSecondPassVectors(ItemStack armorStack, EntityLivingBase wearer, EntityEquipmentSlot slot, float time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            translationVector[0] = time * 0.03003F;
        }

        @Override
        public boolean useForStack(ItemStack stack, EntityLivingBase wearer, EntityEquipmentSlot slot)
        {
            return true;
        }


    };

}