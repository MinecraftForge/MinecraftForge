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

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

/**
 * Interface for controlling ItemStack's effect overlay.
 * These need to be registered using ItemOverlayHandler.registerOverlayHanadler().
 * */
public interface IStackOverlayHandler
{

    /**
     * Vanilla's default/fallback overlay.
     * */
    IStackOverlayHandler VANILLA = new Vanilla();

    /**
     * Vanilla's default Glint texture.
     * */
    ResourceLocation defaultGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    /**
     * Passes vectors for the scaling, rotation, and translation during effect rendering. <br>
     * <b>PLEASE NOTE: These arrays are static, and therefore contain values from the previous handler. You must reset all values or artifacts will be present</b>
     *
     * @param stack the ItemStack
     * @param time the timing of the effect
     * @param scaleVector a vec3 representing the scaling of the effect
     * @param rotationVector a vec4 representing the rotation of the effect
     * @param translationVector a vec3 representing the translation of the effect
     * */
    void manageFirstPassVectors(ItemStack stack, long time, final float[] scaleVector, final float[] rotationVector, final float[] translationVector);

    /**
     * Passes vectors for the scaling, rotation, and translation during effect rendering. <br>
     * <b>PLEASE NOTE: These arrays are static, and therefore contain values from the previous pass. You must reset all values or artifacts will be present</b>
     *
     * @param stack the ItemStack
     * @param time the timing of the effect
     * @param scaleVector a vec3 representing the scaling of the effect
     * @param rotationVector a vec4 representing the rotation of the effect
     * @param translationVector a vec3 representing the translation of the effect
     * */
    void manageSecondPassVectors(ItemStack stack, long time, final float[] scaleVector, final float[] rotationVector, final float[] translationVector);


    /**
     * Used for setting the effect texture.
     *
     * @param stack the ItemStack in question
     *
     * @return the ResourceLocation of the effect texture.
     * */
    ResourceLocation getOverlayTexture(ItemStack stack);

    /**
     * Checks for the first applicable handler when multiple are subscribed to an item.
     *
     * @return true to override vanilla/other subscribers' handlers
     * */
    boolean useForStack(ItemStack stack);

    class Vanilla implements IStackOverlayHandler
    {

        protected Vanilla(){}

        @Override
        public void manageFirstPassVectors(ItemStack itemStack, long time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            scaleVector[0] = 8.0F;
            scaleVector[1] = 8.0F;
            scaleVector[2] = 8.0F;
            rotationVector[0] = -50.0F;
            rotationVector[1] = 0.0F;
            rotationVector[2] = 0.0F;
            rotationVector[3] = 1.0F;
            translationVector[0] = (time % 3000L) / 24000.0F ;
            translationVector[1] = 0.0F;
            translationVector[2] = 0.0F;
        }

        @Override
        public void manageSecondPassVectors(ItemStack itemStack, long time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            rotationVector[0] = 10;
            translationVector[0] = (time % 3873L) / 38984.0F;
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack)
        {
            return defaultGlint;
        }

        @Override
        public boolean useForStack(ItemStack stack)
        {
            return true;
        }

    }

    final class SubscriptionWrapper implements IStackOverlayHandler
    {
        IStackOverlayHandler[] subscribers;

        IStackOverlayHandler cached;

        public SubscriptionWrapper(IStackOverlayHandler toSubscribe)
        {
           subscribers = new IStackOverlayHandler[] {toSubscribe};
        }

        public void addSubscription(IStackOverlayHandler toSubscribe)
        {
            if(Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
                FMLLog.bigWarning("Skipping subscription of {} to StackOverlayHandlers because it's too late in the load cycle..", toSubscribe.getClass());
                return; //Trump bad behavior
            }
            IStackOverlayHandler[] grow = new IStackOverlayHandler[subscribers.length + 1];
            for(int i = 0; i < subscribers.length; i++)
                grow[i] = subscribers[i];
            grow[subscribers.length] = toSubscribe;
            subscribers = grow;
        }

        @Override
        public void manageFirstPassVectors(ItemStack stack, long time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            cached.manageFirstPassVectors(stack, time, scaleVector, rotationVector, translationVector);
        }

        @Override
        public void manageSecondPassVectors(ItemStack stack, long time, float[] scaleVector, float[] rotationVector, float[] translationVector)
        {
            cached.manageSecondPassVectors(stack, time, scaleVector, rotationVector, translationVector);
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack)
        {
            for(int i = 0; i < subscribers.length; i++)
                if((cached = subscribers[i]).useForStack(stack))
                    return cached.getOverlayTexture(stack);
            return (cached = VANILLA).getOverlayTexture(stack);
        }

        @Override
        public boolean useForStack(ItemStack stack)
        {
            return true;
        }
    }

}