package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class Timer
{
    // JAVADOC FIELD $$ field_74282_a
    float ticksPerSecond;
    // JAVADOC FIELD $$ field_74276_f
    private double lastHRTime;
    // JAVADOC FIELD $$ field_74280_b
    public int elapsedTicks;
    // JAVADOC FIELD $$ field_74281_c
    public float renderPartialTicks;
    // JAVADOC FIELD $$ field_74278_d
    public float timerSpeed = 1.0F;
    // JAVADOC FIELD $$ field_74279_e
    public float elapsedPartialTicks;
    // JAVADOC FIELD $$ field_74277_g
    private long lastSyncSysClock;
    // JAVADOC FIELD $$ field_74284_h
    private long lastSyncHRClock;
    private long field_74285_i;
    // JAVADOC FIELD $$ field_74283_j
    private double timeSyncAdjustment = 1.0D;
    private static final String __OBFID = "CL_00000658";

    public Timer(float par1)
    {
        this.ticksPerSecond = par1;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    // JAVADOC METHOD $$ func_74275_a
    public void updateTimer()
    {
        long i = Minecraft.getSystemTime();
        long j = i - this.lastSyncSysClock;
        long k = System.nanoTime() / 1000000L;
        double d0 = (double)k / 1000.0D;

        if (j <= 1000L && j >= 0L)
        {
            this.field_74285_i += j;

            if (this.field_74285_i > 1000L)
            {
                long l = k - this.lastSyncHRClock;
                double d1 = (double)this.field_74285_i / (double)l;
                this.timeSyncAdjustment += (d1 - this.timeSyncAdjustment) * 0.20000000298023224D;
                this.lastSyncHRClock = k;
                this.field_74285_i = 0L;
            }

            if (this.field_74285_i < 0L)
            {
                this.lastSyncHRClock = k;
            }
        }
        else
        {
            this.lastHRTime = d0;
        }

        this.lastSyncSysClock = i;
        double d2 = (d0 - this.lastHRTime) * this.timeSyncAdjustment;
        this.lastHRTime = d0;

        if (d2 < 0.0D)
        {
            d2 = 0.0D;
        }

        if (d2 > 1.0D)
        {
            d2 = 1.0D;
        }

        this.elapsedPartialTicks = (float)((double)this.elapsedPartialTicks + d2 * (double)this.timerSpeed * (double)this.ticksPerSecond);
        this.elapsedTicks = (int)this.elapsedPartialTicks;
        this.elapsedPartialTicks -= (float)this.elapsedTicks;

        if (this.elapsedTicks > 10)
        {
            this.elapsedTicks = 10;
        }

        this.renderPartialTicks = this.elapsedPartialTicks;
    }
}