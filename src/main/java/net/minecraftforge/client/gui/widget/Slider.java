/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nullable;

/**
 * This class is blatantly stolen from iChunUtils with permission.
 *
 * @deprecated This class has a few issues,
 * mainly <a href="https://github.com/MinecraftForge/MinecraftForge/issues/8485">MinecraftForge/MinecraftForge#8485</a>.
 * Use {@link ForgeSlider} instead.
 *
 * @author iChun
 */
@Deprecated(since = "1.18.2", forRemoval = true)
public class Slider extends ExtendedButton
{
    /** The value of this slider control. */
    public double sliderValue = 1.0F;

    public Component dispString;

    /** Is this slider control being dragged. */
    public boolean dragging = false;
    public boolean showDecimal = true;

    public double minValue = 0.0D;
    public double maxValue = 5.0D;
    public int precision = 1;

    @Nullable
    public ISlider parent = null;

    public Component suffix;

    public boolean drawString = true;

    public Slider(int xPos, int yPos, int width, int height, Component prefix, Component suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, OnPress handler)
    {
        this(xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, handler, null);
    }

    public Slider(int xPos, int yPos, int width, int height, Component prefix, Component suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, OnPress handler, @Nullable ISlider par)
    {
        super(xPos, yPos, width, height, prefix, handler);
        minValue = minVal;
        maxValue = maxVal;
        sliderValue = (currentVal - minValue) / (maxValue - minValue);
        dispString = prefix;
        parent = par;
        suffix = suf;
        showDecimal = showDec;
        String val;

        if (showDecimal)
        {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);
            precision = Math.min(val.substring(val.indexOf(".") + 1).length(), 4);
        }
        else
        {
            val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
            precision = 0;
        }

        setMessage(new TextComponent("").append(dispString).append(val).append(suffix));

        drawString = drawStr;
        if(!drawString)
            setMessage(new TextComponent(""));
    }

    public Slider(int xPos, int yPos, Component displayStr, double minVal, double maxVal, double currentVal, OnPress handler, ISlider par)
    {
        this(xPos, yPos, 150, 20, displayStr, new TextComponent(""), minVal, maxVal, currentVal, true, true, handler, par);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    @Override
    public int getYImage(boolean par1)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    @Override
    protected void renderBg(PoseStack poseStack, Minecraft minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (par2 - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();
            }

            GuiUtils.drawContinuousTexturedBox(poseStack, WIDGETS_LOCATION, this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 8, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.sliderValue = (mouseX - (this.x + 4)) / (this.width - 8);
        updateSlider();
        this.dragging = true;
    }

    public void updateSlider()
    {
        if (this.sliderValue < 0.0F)
        {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F)
        {
            this.sliderValue = 1.0F;
        }

        String val;

        if (showDecimal)
        {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);

            if (val.substring(val.indexOf(".") + 1).length() > precision)
            {
                val = val.substring(0, val.indexOf(".") + precision + 1);

                if (val.endsWith("."))
                {
                    val = val.substring(0, val.indexOf(".") + precision);
                }
            }
            else
            {
                while (val.substring(val.indexOf(".") + 1).length() < precision)
                {
                    val = val + "0";
                }
            }
        }
        else
        {
            val = Integer.toString((int)Math.round(sliderValue * (maxValue - minValue) + minValue));
        }

        if(drawString)
        {
            setMessage(new TextComponent("").append(dispString).append(val).append(suffix));
        }

        if (parent != null)
        {
            parent.onChangeSliderValue(this);
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        this.dragging = false;
    }

    public int getValueInt()
    {
        return (int)Math.round(sliderValue * (maxValue - minValue) + minValue);
    }

    public double getValue()
    {
        return sliderValue * (maxValue - minValue) + minValue;
    }

    public void setValue(double d)
    {
        this.sliderValue = (d - minValue) / (maxValue - minValue);
    }

    public static interface ISlider
    {
        void onChangeSliderValue(Slider slider);
    }
}
