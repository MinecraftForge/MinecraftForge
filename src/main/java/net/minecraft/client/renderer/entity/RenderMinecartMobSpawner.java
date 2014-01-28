package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;

@SideOnly(Side.CLIENT)
public class RenderMinecartMobSpawner extends RenderMinecart
{
    private static final String __OBFID = "CL_00001014";

    protected void func_147910_a(EntityMinecartMobSpawner p_147911_1_, float p_147911_2_, Block p_147911_3_, int p_147911_4_)
    {
        super.func_147910_a(p_147911_1_, p_147911_2_, p_147911_3_, p_147911_4_);

        if (p_147911_3_ == Blocks.mob_spawner)
        {
            TileEntityMobSpawnerRenderer.func_147517_a(p_147911_1_.func_98039_d(), p_147911_1_.posX, p_147911_1_.posY, p_147911_1_.posZ, p_147911_2_);
        }
    }

    protected void func_147910_a(EntityMinecart p_147910_1_, float p_147910_2_, Block p_147910_3_, int p_147910_4_)
    {
        this.func_147910_a((EntityMinecartMobSpawner)p_147910_1_, p_147910_2_, p_147910_3_, p_147910_4_);
    }
}