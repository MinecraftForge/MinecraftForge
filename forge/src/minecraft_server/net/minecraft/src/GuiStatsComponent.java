package net.minecraft.src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.forge.DimensionManager;

public class GuiStatsComponent extends JComponent
{
    private static final DecimalFormat field_40573_a = new DecimalFormat("########0.000");

    /** An array containing the columns that make up the memory use graph. */
    private int[] memoryUse = new int[256];

    /**
     * Counts the number of updates. Used as the index into the memoryUse array to display the latest value.
     */
    private int updateCounter = 0;

    /** An array containing the strings displayed in this stats component. */
    private String[] displayStrings = new String[10];
    private final MinecraftServer field_40572_e;

    public GuiStatsComponent(MinecraftServer par1MinecraftServer)
    {
        this.field_40572_e = par1MinecraftServer;
        this.setPreferredSize(new Dimension(356, 246));
        this.setMinimumSize(new Dimension(356, 246));
        this.setMaximumSize(new Dimension(356, 246));
        (new Timer(500, new GuiStatsListener(this))).start();
        this.setBackground(Color.BLACK);
    }

    /**
     * Updates the stat values and calls paint to redraw the component.
     */
    private void updateStats()
    {
        long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        
        if (displayStrings.length < DimensionManager.getIDs().length + 3)
        {
            displayStrings = new String[DimensionManager.getIDs().length + 3];
        }
        for(int x = 0; x < displayStrings.length; x++)
        {
            displayStrings[x] = "";
        }
        
        this.displayStrings[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        this.displayStrings[1] = "Threads: " + NetworkManager.numReadThreads + " + " + NetworkManager.numWriteThreads;
        this.displayStrings[2] = "Avg tick: " + field_40573_a.format(this.func_48551_a(this.field_40572_e.field_40027_f) * 1.0E-6D) + " ms";
        this.displayStrings[3] = "Avg sent: " + (int)this.func_48551_a(this.field_40572_e.field_48080_u) + ", Avg size: " + (int)this.func_48551_a(this.field_40572_e.field_48079_v);
        this.displayStrings[4] = "Avg rec: " + (int)this.func_48551_a(this.field_40572_e.field_48078_w) + ", Avg size: " + (int)this.func_48551_a(this.field_40572_e.field_48082_x);

        int x = 0;
        for (Integer id : DimensionManager.getIDs())
        {
            displayStrings[2 + ++x] = "Lvl " + id + " tick: " + field_40573_a.format(func_48551_a(field_40572_e.worldTickTimes.get(id)) * 10E-6D) + " ms";
            WorldServer world = (WorldServer)DimensionManager.getWorld(id);
            if (world != null && world.chunkProviderServer != null)
            {
                displayStrings[2 + x] += ", " + world.chunkProviderServer.func_46040_d();
            }
        }

        this.memoryUse[this.updateCounter++ & 255] = (int)(this.func_48551_a(this.field_40572_e.field_48079_v) * 100.0D / 12500.0D);
        this.repaint();
    }

    private double func_48551_a(long[] par1ArrayOfLong)
    {
        long var2 = 0L;
        if (par1ArrayOfLong == null) return 0;
        for (int var4 = 0; var4 < par1ArrayOfLong.length; ++var4)
        {
            var2 += par1ArrayOfLong[var4];
        }

        return (double)var2 / (double)par1ArrayOfLong.length;
    }

    public void paint(Graphics par1Graphics)
    {
        par1Graphics.setColor(new Color(16777215));
        par1Graphics.fillRect(0, 0, 356, 246);
        int var2;

        for (var2 = 0; var2 < 256; ++var2)
        {
            int var3 = this.memoryUse[var2 + this.updateCounter & 255];
            par1Graphics.setColor(new Color(var3 + 28 << 16));
            par1Graphics.fillRect(var2, 100 - var3, 1, var3);
        }

        par1Graphics.setColor(Color.BLACK);

        for (var2 = 0; var2 < this.displayStrings.length; ++var2)
        {
            String var4 = this.displayStrings[var2];

            if (var4 != null)
            {
                par1Graphics.drawString(var4, 32, 116 + var2 * 16);
            }
        }
    }

    /**
     * Public static accessor to call updateStats.
     */
    static void update(GuiStatsComponent par0GuiStatsComponent)
    {
        par0GuiStatsComponent.updateStats();
    }
}
