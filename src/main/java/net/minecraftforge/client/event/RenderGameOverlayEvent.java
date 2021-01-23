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

import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.ClientBossInfo;

@Cancelable
public class RenderGameOverlayEvent extends Event
{
    public MatrixStack getMatrixStack()
    {
        return mStack;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public MainWindow getWindow()
    {
        return window;
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

    private final MatrixStack mStack;
    private final float partialTicks;
    private final MainWindow window;
    private final ElementType type;

    public RenderGameOverlayEvent(MatrixStack mStack, float partialTicks, MainWindow window)
    {
        this.mStack = mStack;
        this.partialTicks = partialTicks;
        this.window = window;
        this.type = null;
    }

    private RenderGameOverlayEvent(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type)
    {
        this.mStack = mStack;
        this.partialTicks = parent.getPartialTicks();
        this.window = parent.getWindow();
        this.type = type;
    }

    public static class Pre extends RenderGameOverlayEvent
    {
        public Pre(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type)
        {
            super(mStack, parent, type);
        }
    }

    public static class Post extends RenderGameOverlayEvent
    {
        public Post(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type)
        {
            super(mStack, parent, type);
        }
        @Override public boolean isCancelable(){ return false; }
    }

    public static class BossInfo extends Pre
    {
        private final ClientBossInfo bossInfo;
        private final int x;
        private final int y;
        private int increment;
        public BossInfo(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type, ClientBossInfo bossInfo, int x, int y, int increment)
        {
            super(mStack, parent, type);
            this.bossInfo = bossInfo;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        /**
         * @return The {@link BossInfoClient} currently being rendered
         */
        public ClientBossInfo getBossInfo()
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
        public Text(MatrixStack mStack, RenderGameOverlayEvent parent, ArrayList<String> left, ArrayList<String> right)
        {
            super(mStack, parent, ElementType.TEXT);
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

        public Chat(MatrixStack mStack, RenderGameOverlayEvent parent, int posX, int posY)
        {
            super(mStack, parent, ElementType.CHAT);
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
