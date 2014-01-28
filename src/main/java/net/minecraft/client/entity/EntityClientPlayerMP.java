package net.minecraft.client.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityClientPlayerMP extends EntityPlayerSP
{
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter field_146108_bO;
    private double oldPosX;
    // JAVADOC FIELD $$ field_71177_cg
    private double oldMinY;
    private double oldPosY;
    private double oldPosZ;
    private float oldRotationYaw;
    private float oldRotationPitch;
    // JAVADOC FIELD $$ field_71173_cl
    private boolean wasOnGround;
    // JAVADOC FIELD $$ field_71170_cm
    private boolean shouldStopSneaking;
    private boolean wasSneaking;
    private int field_71168_co;
    // JAVADOC FIELD $$ field_71169_cp
    private boolean hasSetHealth;
    private String field_142022_ce;
    private static final String __OBFID = "CL_00000887";

    public EntityClientPlayerMP(Minecraft p_i45064_1_, World p_i45064_2_, Session p_i45064_3_, NetHandlerPlayClient p_i45064_4_, StatFileWriter p_i45064_5_)
    {
        super(p_i45064_1_, p_i45064_2_, p_i45064_3_, 0);
        this.sendQueue = p_i45064_4_;
        this.field_146108_bO = p_i45064_5_;
    }

    // JAVADOC METHOD $$ func_70097_a
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70691_i
    public void heal(float par1) {}

    // JAVADOC METHOD $$ func_70078_a
    public void mountEntity(Entity par1Entity)
    {
        super.mountEntity(par1Entity);

        if (par1Entity instanceof EntityMinecart)
        {
            this.mc.func_147118_V().func_147682_a(new MovingSoundMinecartRiding(this, (EntityMinecart)par1Entity));
        }
    }

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        if (this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)))
        {
            super.onUpdate();

            if (this.isRiding())
            {
                this.sendQueue.func_147297_a(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.func_147297_a(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            }
            else
            {
                this.sendMotionUpdates();
            }
        }
    }

    // JAVADOC METHOD $$ func_71166_b
    public void sendMotionUpdates()
    {
        boolean flag = this.isSprinting();

        if (flag != this.wasSneaking)
        {
            if (flag)
            {
                this.sendQueue.func_147297_a(new C0BPacketEntityAction(this, 4));
            }
            else
            {
                this.sendQueue.func_147297_a(new C0BPacketEntityAction(this, 5));
            }

            this.wasSneaking = flag;
        }

        boolean flag1 = this.isSneaking();

        if (flag1 != this.shouldStopSneaking)
        {
            if (flag1)
            {
                this.sendQueue.func_147297_a(new C0BPacketEntityAction(this, 1));
            }
            else
            {
                this.sendQueue.func_147297_a(new C0BPacketEntityAction(this, 2));
            }

            this.shouldStopSneaking = flag1;
        }

        double d0 = this.posX - this.oldPosX;
        double d1 = this.boundingBox.minY - this.oldMinY;
        double d2 = this.posZ - this.oldPosZ;
        double d3 = (double)(this.rotationYaw - this.oldRotationYaw);
        double d4 = (double)(this.rotationPitch - this.oldRotationPitch);
        boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.field_71168_co >= 20;
        boolean flag3 = d3 != 0.0D || d4 != 0.0D;

        if (this.ridingEntity != null)
        {
            this.sendQueue.func_147297_a(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
            flag2 = false;
        }
        else if (flag2 && flag3)
        {
            this.sendQueue.func_147297_a(new C03PacketPlayer.C06PacketPlayerPosLook(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
        }
        else if (flag2)
        {
            this.sendQueue.func_147297_a(new C03PacketPlayer.C04PacketPlayerPosition(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
        }
        else if (flag3)
        {
            this.sendQueue.func_147297_a(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
        }
        else
        {
            this.sendQueue.func_147297_a(new C03PacketPlayer(this.onGround));
        }

        ++this.field_71168_co;
        this.wasOnGround = this.onGround;

        if (flag2)
        {
            this.oldPosX = this.posX;
            this.oldMinY = this.boundingBox.minY;
            this.oldPosY = this.posY;
            this.oldPosZ = this.posZ;
            this.field_71168_co = 0;
        }

        if (flag3)
        {
            this.oldRotationYaw = this.rotationYaw;
            this.oldRotationPitch = this.rotationPitch;
        }
    }

    // JAVADOC METHOD $$ func_71040_bB
    public EntityItem dropOneItem(boolean par1)
    {
        int i = par1 ? 3 : 4;
        this.sendQueue.func_147297_a(new C07PacketPlayerDigging(i, 0, 0, 0, 0));
        return null;
    }

    // JAVADOC METHOD $$ func_71012_a
    public void joinEntityItemWithWorld(EntityItem par1EntityItem) {}

    // JAVADOC METHOD $$ func_71165_d
    public void sendChatMessage(String par1Str)
    {
        this.sendQueue.func_147297_a(new C01PacketChatMessage(par1Str));
    }

    // JAVADOC METHOD $$ func_71038_i
    public void swingItem()
    {
        super.swingItem();
        this.sendQueue.func_147297_a(new C0APacketAnimation(this, 1));
    }

    public void respawnPlayer()
    {
        this.sendQueue.func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    // JAVADOC METHOD $$ func_70665_d
    protected void damageEntity(DamageSource par1DamageSource, float par2)
    {
        if (!this.isEntityInvulnerable())
        {
            this.setHealth(this.getHealth() - par2);
        }
    }

    // JAVADOC METHOD $$ func_71053_j
    public void closeScreen()
    {
        this.sendQueue.func_147297_a(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.func_92015_f();
    }

    public void func_92015_f()
    {
        this.inventory.setItemStack((ItemStack)null);
        super.closeScreen();
    }

    // JAVADOC METHOD $$ func_71150_b
    public void setPlayerSPHealth(float par1)
    {
        if (this.hasSetHealth)
        {
            super.setPlayerSPHealth(par1);
        }
        else
        {
            this.setHealth(par1);
            this.hasSetHealth = true;
        }
    }

    // JAVADOC METHOD $$ func_71064_a
    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            if (par1StatBase.isIndependent)
            {
                super.addStat(par1StatBase, par2);
            }
        }
    }

    // JAVADOC METHOD $$ func_71016_p
    public void sendPlayerAbilities()
    {
        this.sendQueue.func_147297_a(new C13PacketPlayerAbilities(this.capabilities));
    }

    protected void func_110318_g()
    {
        this.sendQueue.func_147297_a(new C0BPacketEntityAction(this, 6, (int)(this.getHorseJumpPower() * 100.0F)));
    }

    public void func_110322_i()
    {
        this.sendQueue.func_147297_a(new C0BPacketEntityAction(this, 7));
    }

    public void func_142020_c(String par1Str)
    {
        this.field_142022_ce = par1Str;
    }

    public String func_142021_k()
    {
        return this.field_142022_ce;
    }

    public StatFileWriter func_146107_m()
    {
        return this.field_146108_bO;
    }
}