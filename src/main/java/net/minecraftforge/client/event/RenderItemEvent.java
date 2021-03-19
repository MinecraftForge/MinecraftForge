/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * Subsidiaries to this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS} throughout
 * the process of Item rendering, allowing adjustments to the default functionality.
 * <br>
 * In sequence, subsidiaries to this event are fired:
 * <ol>
 *     <li>Before an Item model is rendered.</li>
 *     <li>Before an ItemStack count is rendered.</li>
 *     <li>Before an ItemStack durability is rendered.</li>
 *     <li>Before an Item cooldown is rendered.</li>
 *     <li>After the entire Item overlay has been rendered.</li>
 * </ol>
 */
public class RenderItemEvent extends Event
{
    public MatrixStack getMatrixStack()
    {
        return matrixStack;
    }
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    private final MatrixStack matrixStack;
    private final ItemStack itemStack;

    private RenderItemEvent(MatrixStack matrixStack, ItemStack itemStack)
    {
        this.matrixStack = matrixStack;
        this.itemStack = itemStack;
    }

    /**
     * The RenderItemEvent.Model event is fired immediately before the model of an Item is rendered. This enables
     * per-stack adjustment of what is displayed behind a rendered Item.
     * <br>
     * This event does not allow direct control over how Item models are rendered or whether they are rendered at all,
     * though it does provide information that is used in the normal Item model rendering.
     * <br>
     * This event is not fired when empty Items are rendered.
     */
    public static class Model extends RenderItemEvent
    {
        public IRenderTypeBuffer getRenderTypeBuffer()
        {
            return renderTypeBuffer;
        }

        public int getCombinedLight()
        {
            return combinedLight;
        }

        public int getCombinedOverlay()
        {
            return combinedOverlay;
        }

        public IBakedModel getBakedModel()
        {
            return bakedModel;
        }

        private final IRenderTypeBuffer renderTypeBuffer;
        private final int combinedLight;
        private final int combinedOverlay;
        private final IBakedModel bakedModel;

        public Model(MatrixStack matrixStack, ItemStack itemStack, IRenderTypeBuffer renderTypeBuffer,
                     int combinedLight, int combinedOverlay, IBakedModel bakedModel)
        {
            super(matrixStack, itemStack);

            this.renderTypeBuffer = renderTypeBuffer;
            this.combinedLight = combinedLight;
            this.combinedOverlay = combinedOverlay;
            this.bakedModel = bakedModel;
        }
    }

    /**
     * The RenderItemEvent.Overlay event is fired at three distinct points during the rendering of an Item overlay.
     * <br>
     * Accordingly, the event has three distinct types, as enumerated by {@link OverlayPart}. The specification of an
     * {@link OverlayPart} is required for the firing of this event, and it may not be null.
     * <ul>
     *     <li>{@link OverlayPart#COUNT} Overlay events are fired immediately before the count of an ItemStack is
     *     rendered. If canceled, the Item count will not be rendered.</li>
     *     <li>{@link OverlayPart#DURABILITY} Overlay events are fired immediately before the durability of an ItemStack
     *     is rendered, iff the durability bar of the Item should be rendered. If canceled, the durability bar of
     *     applicable Items will not be rendered.</li>
     *     <li>{@link OverlayPart#COOLDOWN} Overlay events are fired immediately before the cooldown of an Item is
     *     rendered, iff the cooldown is nonzero. If canceled, the cooldown of applicable Items will not be rendered.
     *     </li>
     *     <li>{@link OverlayPart#EXTRA} Overlay events are fired after the overlay of an Item has been rendered,
     *     regardless of the cancellation of its preceding Overlay events. These events cannot be canceled, as their
     *     cancellation would have no effect.</li>
     * </ul>
     * This event is cancelable iff its {@link OverlayPart} is not {@link OverlayPart#EXTRA}.
     */
    @Cancelable
    public static class Overlay extends RenderItemEvent
    {
        public FontRenderer font()
        {
            return fontRenderer;
        }

        public int getX()
        {
            return xPosition;
        }

        public int getY()
        {
            return yPosition;
        }

        public OverlayPart getOverlayPart()
        {
            return overlayPart;
        }

        private final FontRenderer fontRenderer;
        private final int xPosition, yPosition;
        private final OverlayPart overlayPart;

        /**
         * OverlayPart enumerates the distinct parts of an Item overlay that can be rendered.
         * <ul>
         *     <li>COUNT refers to the rendering of the count of an ItemStack.</li>
         *     <li>DURABILITY refers to the rendering of the durability of an applicable ItemStack.</li>
         *     <li>COOLDOWN refers to the rendering of the cooldown of applicable Items.</li>
         *     <li>EXTRA refers to the rendering of anything above the previous three overlay parts.</li>
         * </ul>
         */
        public static enum OverlayPart
        {
            COUNT,
            DURABILITY,
            COOLDOWN,
            EXTRA
        }

        public Overlay(FontRenderer fontRenderer, MatrixStack matrixStack, ItemStack itemStack,
                       int xPosition, int yPosition, @Nonnull OverlayPart overlayPart)
        {
            super(matrixStack, itemStack);

            this.fontRenderer = fontRenderer;
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            this.overlayPart = overlayPart;
        }

        @Override
        public boolean isCancelable()
        {
            return overlayPart != OverlayPart.EXTRA;
        }
    }
}
