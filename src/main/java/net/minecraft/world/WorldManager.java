package net.minecraft.world;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess
{
    // JAVADOC FIELD $$ field_72783_a
    private MinecraftServer mcServer;
    // JAVADOC FIELD $$ field_72782_b
    private WorldServer theWorldServer;
    private static final String __OBFID = "CL_00001433";

    public WorldManager(MinecraftServer par1MinecraftServer, WorldServer par2WorldServer)
    {
        this.mcServer = par1MinecraftServer;
        this.theWorldServer = par2WorldServer;
    }

    // JAVADOC METHOD $$ func_72708_a
    public void spawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12) {}

    // JAVADOC METHOD $$ func_72703_a
    public void onEntityCreate(Entity par1Entity)
    {
        this.theWorldServer.getEntityTracker().addEntityToTracker(par1Entity);
    }

    // JAVADOC METHOD $$ func_72709_b
    public void onEntityDestroy(Entity par1Entity)
    {
        this.theWorldServer.getEntityTracker().removeEntityFromAllTrackingPlayers(par1Entity);
    }

    // JAVADOC METHOD $$ func_72704_a
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9)
    {
        this.mcServer.getConfigurationManager().func_148541_a(par2, par4, par6, par8 > 1.0F ? (double)(16.0F * par8) : 16.0D, this.theWorldServer.provider.dimensionId, new S29PacketSoundEffect(par1Str, par2, par4, par6, par8, par9));
    }

    // JAVADOC METHOD $$ func_85102_a
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10)
    {
        this.mcServer.getConfigurationManager().func_148543_a(par1EntityPlayer, par3, par5, par7, par9 > 1.0F ? (double)(16.0F * par9) : 16.0D, this.theWorldServer.provider.dimensionId, new S29PacketSoundEffect(par2Str, par3, par5, par7, par9, par10));
    }

    public void func_147585_a(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {}

    public void func_147586_a(int p_147586_1_, int p_147586_2_, int p_147586_3_)
    {
        this.theWorldServer.getPlayerManager().func_151250_a(p_147586_1_, p_147586_2_, p_147586_3_);
    }

    public void func_147588_b(int p_147588_1_, int p_147588_2_, int p_147588_3_) {}

    // JAVADOC METHOD $$ func_72702_a
    public void playRecord(String par1Str, int par2, int par3, int par4) {}

    // JAVADOC METHOD $$ func_72706_a
    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
        this.mcServer.getConfigurationManager().func_148543_a(par1EntityPlayer, (double)par3, (double)par4, (double)par5, 64.0D, this.theWorldServer.provider.dimensionId, new S28PacketEffect(par2, par3, par4, par5, par6, false));
    }

    public void broadcastSound(int par1, int par2, int par3, int par4, int par5)
    {
        this.mcServer.getConfigurationManager().func_148540_a(new S28PacketEffect(par1, par2, par3, par4, par5, true));
    }

    public void func_147587_b(int p_147587_1_, int p_147587_2_, int p_147587_3_, int p_147587_4_, int p_147587_5_)
    {
        Iterator iterator = this.mcServer.getConfigurationManager().playerEntityList.iterator();

        while (iterator.hasNext())
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)iterator.next();

            if (entityplayermp != null && entityplayermp.worldObj == this.theWorldServer && entityplayermp.func_145782_y() != p_147587_1_)
            {
                double d0 = (double)p_147587_2_ - entityplayermp.posX;
                double d1 = (double)p_147587_3_ - entityplayermp.posY;
                double d2 = (double)p_147587_4_ - entityplayermp.posZ;

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D)
                {
                    entityplayermp.playerNetServerHandler.func_147359_a(new S25PacketBlockBreakAnim(p_147587_1_, p_147587_2_, p_147587_3_, p_147587_4_, p_147587_5_));
                }
            }
        }
    }

    public void func_147584_b() {}
}