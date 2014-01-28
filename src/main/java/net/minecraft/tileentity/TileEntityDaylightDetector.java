package net.minecraft.tileentity;

import net.minecraft.block.BlockDaylightDetector;

public class TileEntityDaylightDetector extends TileEntity
{
    private static final String __OBFID = "CL_00000350";

    public void func_145845_h()
    {
        if (this.field_145850_b != null && !this.field_145850_b.isRemote && this.field_145850_b.getTotalWorldTime() % 20L == 0L)
        {
            this.field_145854_h = this.func_145838_q();

            if (this.field_145854_h instanceof BlockDaylightDetector)
            {
                ((BlockDaylightDetector)this.field_145854_h).func_149957_e(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
            }
        }
    }
}