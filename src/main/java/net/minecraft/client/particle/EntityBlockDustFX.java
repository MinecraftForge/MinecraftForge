package net.minecraft.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityBlockDustFX extends EntityDiggingFX
{
    private static final String __OBFID = "CL_00000931";

    public EntityBlockDustFX(World p_i45072_1_, double p_i45072_2_, double p_i45072_4_, double p_i45072_6_, double p_i45072_8_, double p_i45072_10_, double p_i45072_12_, Block p_i45072_14_, int p_i45072_15_)
    {
        super(p_i45072_1_, p_i45072_2_, p_i45072_4_, p_i45072_6_, p_i45072_8_, p_i45072_10_, p_i45072_12_, p_i45072_14_, p_i45072_15_);
        this.motionX = p_i45072_8_;
        this.motionY = p_i45072_10_;
        this.motionZ = p_i45072_12_;
    }
}