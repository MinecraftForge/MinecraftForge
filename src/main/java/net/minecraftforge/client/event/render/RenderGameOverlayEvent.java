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

package net.minecraftforge.client.event.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;

/**
 * Fired when an overlay element is rendered to the game window.
 * See the two subclasses for listening to before and after rendering.
 *
 * <p>An overlay that is not normally active cannot be forced to render, and this event will not fire. </p>
 *
 * @see RenderGameOverlayEvent.Pre
 * @see RenderGameOverlayEvent.Post
 * @see ForgeIngameGui
 */
@Cancelable
public class RenderGameOverlayEvent extends Event
{
    /**
     * The types of the different overlay elements that can be rendered
     */
    public enum ElementType
    {
        /**
         * Represents all of the overlay elements; cancelling the {@link RenderGameOverlayEvent.Pre} with this type
         * will suppress rendering of all overlay elements.
         */
        ALL,
        /**
         * An in-game overlay layer.
         *
         * @see IIngameOverlay
         * @see RenderGameOverlayEvent.PreLayer
         * @see RenderGameOverlayEvent.PostLayer
         */
        LAYER,
        /**
         * An individual boss event bar.
         *
         * @see RenderGameOverlayEvent.BossEvent
         */
        BOSS_EVENT,    // Individual boss bar
        /**
         * For adding text to be rendered on the screen (after the debug screen text, if visible).
         *
         * @see RenderGameOverlayEvent.Text
         */
        TEXT,
        /**
         * The chat message window overlay.
         *
         * @see RenderGameOverlayEvent.Chat
         */
        CHAT,
        /**
         * The player list overlay, shown when the <kdb>TAB</kdb> key (or other keybind) is pressed.
         *
         * <em>See {@code ForgeIngameGui#renderPlayerList}</em>
         */
        PLAYER_LIST,
        /**
         * The debug overlay.
         *
         * <em>See {@code ForgeIngameGui#renderHUDText.}</em>
         */
        DEBUG
    }

    private final PoseStack poseStack;
    private final float partialTick;
    private final Window window;
    private final ElementType type;

    public RenderGameOverlayEvent(PoseStack poseStack, float partialTick, Window window)
    {
        this.poseStack = poseStack;
        this.partialTick = partialTick;
        this.window = window;
        this.type = null;
    }

    private RenderGameOverlayEvent(PoseStack poseStack, RenderGameOverlayEvent parent, ElementType type)
    {
        this.poseStack = poseStack;
        this.partialTick = parent.getPartialTick();
        this.window = parent.getWindow();
        this.type = type;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the partial ticks}
     */
    public float getPartialTick()
    {
        return partialTick;
    }

    /**
     * {@return the game window}
     */
    public Window getWindow()
    {
        return window;
    }

    /**
     * {@return the type of the overlay element being rendered}
     */
    public ElementType getType()
    {
        return type;
    }

    /**
     * Fired <b>before</b> an active overlay element is rendered to the screen.
     * <em>See the three sub-classes for specific special overlay elements. </em>
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the overlay corresponding to this event will not be rendered, and the
     * corresponding {@link RenderGameOverlayEvent.Post} event is not fired. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see RenderGameOverlayEvent.PreLayer
     * @see RenderGameOverlayEvent.BossEvent
     * @see RenderGameOverlayEvent.Text
     * @see RenderGameOverlayEvent.Chat
     */
    public static class Pre extends RenderGameOverlayEvent
    {
        public Pre(PoseStack poseStack, RenderGameOverlayEvent parent, ElementType type)
        {
            super(poseStack, parent, type);
        }
    }

    /**
     * Fired <b>after</b> an active overlay element is rendered to the screen, if the corresponding
     * {@link RenderGameOverlayEvent.Pre} is not cancelled.
     * <em>See the specialized {@link RenderGameOverlayEvent.PostLayer} for individual overlay layer.</em>
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class Post extends RenderGameOverlayEvent
    {
        public Post(PoseStack poseStack, RenderGameOverlayEvent parent, ElementType type)
        {
            super(poseStack, parent, type);
        }

        @Override
        public boolean isCancelable()
        {
            return false;
        }
    }

    /**
     * Fired <b>before</b> an in-game overlay layer is rendered to the screen.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the overlay layer in this event will not be rendered, and the
     * corresponding {@link RenderGameOverlayEvent.PostLayer} event is not fired. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see RenderGameOverlayEvent.PostLayer
     */
    public static class PreLayer extends Pre
    {
        private final IIngameOverlay overlay;

        public PreLayer(PoseStack mStack, RenderGameOverlayEvent parent, IIngameOverlay overlay)
        {
            super(mStack, parent, ElementType.LAYER);
            this.overlay = overlay;
        }

        /**
         * @return the in-game overlay layer being rendered
         */
        public IIngameOverlay getOverlay()
        {
            return overlay;
        }
    }

    /**
     * Fired <b>after</b> an in-game overlay layer is rendered to the screen, if the corresponding
     * {@link RenderGameOverlayEvent.PreLayer} is not cancelled.
     *
     * <p>This event is not {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     */
    public static class PostLayer extends Post
    {
        private final IIngameOverlay overlay;

        public PostLayer(PoseStack mStack, RenderGameOverlayEvent parent, IIngameOverlay overlay)
        {
            super(mStack, parent, ElementType.LAYER);
            this.overlay = overlay;
        }

        /**
         * @return the in-game overlay layer being rendered
         */
        public IIngameOverlay getOverlay()
        {
            return overlay;
        }
    }

    /**
     * Fired <b>before</b> a boss health bar is rendered to the screen.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the boss health bar corresponding to this event will not be rendered, and the
     * corresponding {@link RenderGameOverlayEvent.Post} is not fired. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ElementType#BOSS_EVENT
     */
    public static class BossEvent extends Pre
    {
        private final LerpingBossEvent bossEvent;
        private final int x;
        private final int y;
        private int increment;

        public BossEvent(PoseStack mStack, RenderGameOverlayEvent parent, ElementType type, LerpingBossEvent bossEvent, int x, int y, int increment)
        {
            super(mStack, parent, type);
            this.bossEvent = bossEvent;
            this.x = x;
            this.y = y;
            this.increment = increment;
        }

        /**
         * {@return the boss health bar currently being rendered}
         */
        public LerpingBossEvent getBossEvent()
        {
            return bossEvent;
        }

        /**
         * {@return the X position of the boss health bar}
         */
        public int getX()
        {
            return x;
        }

        /**
         * {@return the Y position of the boss health bar}
         */
        public int getY()
        {
            return y;
        }

        /**
         * {@return the Y position increment before rendering the next boss health bar}
         */
        public int getIncrement()
        {
            return increment;
        }


        /**
         * Sets the Y position increment before rendering the next boss health bar.
         *
         * @param increment the new Y position increment
         */
        public void setIncrement(int increment)
        {
            this.increment = increment;
        }
    }

    /**
     * Fired <b>before</b> textual information is rendered to the debug screen.
     * This can be used to add or remove text information.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the text will not be rendered; <em>this includes the debug overlay text<em/>. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ElementType#TEXT
     */
    public static class Text extends Pre
    {
        private final ArrayList<String> left;
        private final ArrayList<String> right;

        public Text(PoseStack poseStack, RenderGameOverlayEvent parent, ArrayList<String> left, ArrayList<String> right)
        {
            super(poseStack, parent, ElementType.TEXT);
            this.left = left;
            this.right = right;
        }

        /**
         * @return the modifiable list of text to render on the left side
         */
        public ArrayList<String> getLeft()
        {
            return left;
        }

        /**
         * @return the modifiable list of text to render on the right side
         */
        public ArrayList<String> getRight()
        {
            return right;
        }
    }

    /**
     * Fired <b>before</b> the chat messages overlay is rendered to the screen.
     *
     * <p>This event is {@linkplain Cancelable cancelable}, and does not {@linkplain HasResult have a result}.
     * If this event is cancelled, then the chat messages overlay will not be rendered. </p>
     *
     * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
     * only on the {@linkplain LogicalSide#CLIENT logical client}. </p>
     *
     * @see ElementType#CHAT
     */
    public static class Chat extends Pre
    {
        private int posX;
        private int posY;

        public Chat(PoseStack poseStack, RenderGameOverlayEvent parent, int posX, int posY)
        {
            super(poseStack, parent, ElementType.CHAT);
            this.setPosX(posX);
            this.setPosY(posY);
        }

        /**
         * {@return the X position of the chat messages overlay}
         */
        public int getPosX()
        {
            return posX;
        }

        /**
         * Sets the new X position for rendering the chat messages overlay
         *
         * @param posX the new X position
         */
        public void setPosX(int posX)
        {
            this.posX = posX;
        }

        /**
         * {@return the Y position of the chat messages overlay}
         */
        public int getPosY()
        {
            return posY;
        }

        /**
         * Sets the new Y position for rendering the chat messages overlay
         *
         * @param posY the new y position
         */
        public void setPosY(int posY)
        {
            this.posY = posY;
        }
    }
}
