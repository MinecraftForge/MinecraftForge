package net.minecraft.src;

import java.awt.Canvas;
import java.awt.Dimension;

class CanvasCrashReport extends Canvas
{
    public CanvasCrashReport(int par1)
    {
        this.setPreferredSize(new Dimension(par1, par1));
        this.setMinimumSize(new Dimension(par1, par1));
    }
}
