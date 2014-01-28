package net.minecraft.world.demo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager
{
    private boolean field_73105_c;
    private boolean demoTimeExpired;
    private int field_73104_e;
    private int field_73102_f;
    private static final String __OBFID = "CL_00001429";

    public DemoWorldManager(World par1World)
    {
        super(par1World);
    }

    public void updateBlockRemoving()
    {
        super.updateBlockRemoving();
        ++this.field_73102_f;
        long i = this.theWorld.getTotalWorldTime();
        long j = i / 24000L + 1L;

        if (!this.field_73105_c && this.field_73102_f > 20)
        {
            this.field_73105_c = true;
            this.thisPlayerMP.playerNetServerHandler.func_147359_a(new S2BPacketChangeGameState(5, 0.0F));
        }

        this.demoTimeExpired = i > 120500L;

        if (this.demoTimeExpired)
        {
            ++this.field_73104_e;
        }

        if (i % 24000L == 500L)
        {
            if (j <= 6L)
            {
                this.thisPlayerMP.func_145747_a(new ChatComponentTranslation("demo.day." + j, new Object[0]));
            }
        }
        else if (j == 1L)
        {
            if (i == 100L)
            {
                this.thisPlayerMP.playerNetServerHandler.func_147359_a(new S2BPacketChangeGameState(5, 101.0F));
            }
            else if (i == 175L)
            {
                this.thisPlayerMP.playerNetServerHandler.func_147359_a(new S2BPacketChangeGameState(5, 102.0F));
            }
            else if (i == 250L)
            {
                this.thisPlayerMP.playerNetServerHandler.func_147359_a(new S2BPacketChangeGameState(5, 103.0F));
            }
        }
        else if (j == 5L && i % 24000L == 22000L)
        {
            this.thisPlayerMP.func_145747_a(new ChatComponentTranslation("demo.day.warning", new Object[0]));
        }
    }

    // JAVADOC METHOD $$ func_73101_e
    private void sendDemoReminder()
    {
        if (this.field_73104_e > 100)
        {
            this.thisPlayerMP.func_145747_a(new ChatComponentTranslation("demo.reminder", new Object[0]));
            this.field_73104_e = 0;
        }
    }

    // JAVADOC METHOD $$ func_73074_a
    public void onBlockClicked(int par1, int par2, int par3, int par4)
    {
        if (this.demoTimeExpired)
        {
            this.sendDemoReminder();
        }
        else
        {
            super.onBlockClicked(par1, par2, par3, par4);
        }
    }

    public void uncheckedTryHarvestBlock(int par1, int par2, int par3)
    {
        if (!this.demoTimeExpired)
        {
            super.uncheckedTryHarvestBlock(par1, par2, par3);
        }
    }

    // JAVADOC METHOD $$ func_73084_b
    public boolean tryHarvestBlock(int par1, int par2, int par3)
    {
        return this.demoTimeExpired ? false : super.tryHarvestBlock(par1, par2, par3);
    }

    // JAVADOC METHOD $$ func_73085_a
    public boolean tryUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack)
    {
        if (this.demoTimeExpired)
        {
            this.sendDemoReminder();
            return false;
        }
        else
        {
            return super.tryUseItem(par1EntityPlayer, par2World, par3ItemStack);
        }
    }

    // JAVADOC METHOD $$ func_73078_a
    public boolean activateBlockOrUseItem(EntityPlayer par1EntityPlayer, World par2World, ItemStack par3ItemStack, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (this.demoTimeExpired)
        {
            this.sendDemoReminder();
            return false;
        }
        else
        {
            return super.activateBlockOrUseItem(par1EntityPlayer, par2World, par3ItemStack, par4, par5, par6, par7, par8, par9, par10);
        }
    }
}