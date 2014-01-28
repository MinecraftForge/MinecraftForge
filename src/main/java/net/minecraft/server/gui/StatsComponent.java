package net.minecraft.server.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;

@SideOnly(Side.SERVER)
public class StatsComponent extends JComponent
{
    private static final DecimalFormat field_120040_a = new DecimalFormat("########0.000");
    private int[] field_120038_b = new int[256];
    private int field_120039_c;
    private String[] field_120036_d = new String[11];
    private final MinecraftServer field_120037_e;
    private static final String __OBFID = "CL_00001796";

    public StatsComponent(MinecraftServer par1MinecraftServer)
    {
        this.field_120037_e = par1MinecraftServer;
        this.setPreferredSize(new Dimension(456, 246));
        this.setMinimumSize(new Dimension(456, 246));
        this.setMaximumSize(new Dimension(456, 246));
        (new Timer(500, new ActionListener()
        {
            private static final String __OBFID = "CL_00001797";
            public void actionPerformed(ActionEvent par1ActionEvent)
            {
                StatsComponent.this.func_120034_a();
            }
        })).start();
        this.setBackground(Color.BLACK);
    }

    private void func_120034_a()
    {
        long i = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        this.field_120036_d[0] = "Memory use: " + i / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        this.field_120036_d[1] = "Avg tick: " + field_120040_a.format(this.func_120035_a(this.field_120037_e.tickTimeArray) * 1.0E-6D) + " ms";
        this.repaint();
    }

    private double func_120035_a(long[] par1ArrayOfLong)
    {
        long i = 0L;

        for (int j = 0; j < par1ArrayOfLong.length; ++j)
        {
            i += par1ArrayOfLong[j];
        }

        return (double)i / (double)par1ArrayOfLong.length;
    }

    public void paint(Graphics par1Graphics)
    {
        par1Graphics.setColor(new Color(16777215));
        par1Graphics.fillRect(0, 0, 456, 246);
        int i;

        for (i = 0; i < 256; ++i)
        {
            int j = this.field_120038_b[i + this.field_120039_c & 255];
            par1Graphics.setColor(new Color(j + 28 << 16));
            par1Graphics.fillRect(i, 100 - j, 1, j);
        }

        par1Graphics.setColor(Color.BLACK);

        for (i = 0; i < this.field_120036_d.length; ++i)
        {
            String s = this.field_120036_d[i];

            if (s != null)
            {
                par1Graphics.drawString(s, 32, 116 + i * 16);
            }
        }
    }
}