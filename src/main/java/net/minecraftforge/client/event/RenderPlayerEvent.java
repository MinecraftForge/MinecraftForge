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

package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final RenderPlayer renderer;
    private final float partialRenderTick;
    private final double x;
    private final double y;
    private final double z;

    public RenderPlayerEvent(EntityPlayer player, RenderPlayer renderer, float partialRenderTick, double x, double y, double z)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RenderPlayer getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(EntityPlayer player, RenderPlayer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
        @Deprecated
        public Pre(EntityPlayer player, RenderPlayer renderer, float tick){ this(player, renderer, tick, 0D, 0D, 0D); }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(EntityPlayer player, RenderPlayer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
        @Deprecated
        public Post(EntityPlayer player, RenderPlayer renderer, float tick){ this(player, renderer, tick, 0D, 0D, 0D); }
    }
    
    @Deprecated
    public abstract static class Specials extends RenderPlayerEvent
    {
        public Specials(EntityPlayer player, RenderPlayer renderer, float partialTicks)
        {
            super(player, renderer, partialTicks, 0D, 0D, 0D);
        }

        @Cancelable
        public static class Pre extends Specials
        {
            private boolean renderHelmet = true;
            private boolean renderCape = true;
            private boolean renderItem = true;
            public Pre(EntityPlayer player, RenderPlayer renderer, float partialTicks){ super(player, renderer, partialTicks); }

            public boolean shouldRenderHelmet() { return renderHelmet; }
            public void setRenderHelmet(boolean renderHelmet) { this.renderHelmet = renderHelmet; }
            public boolean shouldRenderCape() { return renderCape; }
            public void setRenderCape(boolean renderCape) { this.renderCape = renderCape; }
            public boolean shouldRenderItem() { return renderItem; }
            public void setRenderItem(boolean renderItem) { this.renderItem = renderItem; }
        }

        public static class Post extends Specials
        {
            public Post(EntityPlayer player, RenderPlayer renderer, float partialTicks){ super(player, renderer, partialTicks); }
        }
    }

    @Deprecated
    public static class SetArmorModel extends RenderPlayerEvent
    {
        private int result = -1;
        private final int slot;
        @Nonnull
        private final ItemStack stack;
        public SetArmorModel(EntityPlayer player, RenderPlayer renderer, int slot, float partialTick, @Nonnull ItemStack stack)
        {
            super(player, renderer, partialTick, 0D, 0D, 0D);
            this.slot = slot;
            this.stack = stack;
        }

        /**
         * Setting this to any value besides -1 will result in the function being
         * Immediately exited with the return value specified.
         */
        public int getResultValue()
        {
            return result;
        }

        public void setResult(int result)
        {
            this.result = result;
        }

        public int getSlot()
        {
            return slot;
        }

        @Nonnull
        public ItemStack getStack()
        {
            return stack;
        }
    }
}