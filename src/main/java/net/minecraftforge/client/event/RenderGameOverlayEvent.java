/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import java.util.ArrayList;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.ScaledResolution;

@Cancelable
public class RenderGameOverlayEvent extends Event
{
    public float getPartialTicks()
    {
        return partialTicks;
    }

    public ScaledResolution getResolution()
    {
        return resolution;
    }

    public ElementType getType()
    {
        return type;
    }

    public static enum ElementType
    {
        ALL,
        HELMET,
        PORTAL,
        CROSSHAIRS,
        BOSSHEALTH, // All boss bars
        BOSSINFO,    // Individual boss bar
        ARMOR,
        HEALTH,
        FOOD,
        AIR,
        HOTBAR,
        EXPERIENCE,
        TEXT,
        HEALTHMOUNT,
        JUMPBAR,
        CHAT,
        PLAYER_LIST,
        DEBUG,
        POTION_ICONS,
        SUBTITLES,
        FPS_GRAPH,
        VIGNETTE
    }

    private final float partialTicks;
    private final ScaledResolution resolution;
    private final ElementType type;

    public RenderGameOverlayEvent(float partialTicks, ScaledResolution resolution)
    {
        this.partialTicks = partialTicks;
        this.resolution = resolution;
        this.type = null;
    }

    private RenderGameOverlayEvent(RenderGameOverlayEvent parent, ElementType type)
    {
        this.partialTicks = parent.getPartialTicks();
        this.resolution = parent.getResolution();
        this.type = type;
    }

    public static class Pre extends RenderGameOverlayEvent
    {
        public Pre(RenderGameOverlayEvent parent, ElementType type)
        {
            super(parent, type);
        }
    }

    public static class Post extends RenderGameOverlayEvent
    {
        public Post(RenderGameOverlayEvent parent, ElementType type)
        {
            super(parent, type);
        }
        @Override public boolean isCancelable(){ return false; }
    }

    public static class BossInfo extends Pre
    {
        private final BossInfoClient bossInfo;
        private final int x;
        private final int y;
        private int increment;
        public BossInfo(RenderGameOverlayEvent parent, ElementType type, BossInfoClient bossInfo, int x, int y, int increment)
        {
            super(parent, type);
            this.bossInfo = bossInfo;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        /**
         * @return The {@link BossInfoClient} currently being rendered
         */
        public BossInfoClient getBossInfo()
        {
            return bossInfo;
        }

        /**
         * @return The current x position we are rendering at
         */
        public int getX()
        {
            return x;
        }

        /**
         * @return The current y position we are rendering at
         */
        public int getY()
        {
            return y;
        }

        /**
         * @return How much to move down before rendering the next bar
         */
        public int getIncrement()
        {
            return increment;
        }

        /**
         * Sets the amount to move down before rendering the next bar
         * @param increment The increment to set
         */
        public void setIncrement(int increment)
        {
            this.increment = increment;
        }
    }

    public static class Text extends Pre
    {
        private final ArrayList<String> left;
        private final ArrayList<String> right;
        public Text(RenderGameOverlayEvent parent, ArrayList<String> left, ArrayList<String> right)
        {
            super(parent, ElementType.TEXT);
            this.left = left;
            this.right = right;
        }

        public ArrayList<String> getLeft()
        {
            return left;
        }

        public ArrayList<String> getRight()
        {
            return right;
        }
    }

    public static class Chat extends Pre
    {
        private int posX;
        private int posY;

        public Chat(RenderGameOverlayEvent parent, int posX, int posY)
        {
            super(parent, ElementType.CHAT);
            this.setPosX(posX);
            this.setPosY(posY);
        }

        public int getPosX()
        {
            return posX;
        }

        public void setPosX(int posX)
        {
            this.posX = posX;
        }

        public int getPosY()
        {
            return posY;
        }

        public void setPosY(int posY)
        {
            this.posY = posY;
        }
    }
}
