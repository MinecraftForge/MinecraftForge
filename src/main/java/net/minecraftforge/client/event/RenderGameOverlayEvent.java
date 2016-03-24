package net.minecraftforge.client.event;

import java.util.ArrayList;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

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
        BOSSHEALTH,
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
        SUBTITLES
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
