package net.minecraft.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker
{
    private final List field_94556_a = new ArrayList();
    // JAVADOC FIELD $$ field_94554_b
    private final EntityLivingBase fighter;
    private int field_94555_c;
    private boolean field_94552_d;
    private boolean field_94553_e;
    private String field_94551_f;
    private static final String __OBFID = "CL_00001520";

    public CombatTracker(EntityLivingBase par1EntityLivingBase)
    {
        this.fighter = par1EntityLivingBase;
    }

    public void func_94545_a()
    {
        this.func_94542_g();

        if (this.fighter.isOnLadder())
        {
            Block block = this.fighter.worldObj.func_147439_a(MathHelper.floor_double(this.fighter.posX), MathHelper.floor_double(this.fighter.boundingBox.minY), MathHelper.floor_double(this.fighter.posZ));

            if (block == Blocks.ladder)
            {
                this.field_94551_f = "ladder";
            }
            else if (block == Blocks.vine)
            {
                this.field_94551_f = "vines";
            }
        }
        else if (this.fighter.isInWater())
        {
            this.field_94551_f = "water";
        }
    }

    public void func_94547_a(DamageSource par1DamageSource, float par2, float par3)
    {
        this.func_94549_h();
        this.func_94545_a();
        CombatEntry combatentry = new CombatEntry(par1DamageSource, this.fighter.ticksExisted, par2, par3, this.field_94551_f, this.fighter.fallDistance);
        this.field_94556_a.add(combatentry);
        this.field_94555_c = this.fighter.ticksExisted;
        this.field_94553_e = true;
        this.field_94552_d |= combatentry.func_94559_f();
    }

    public IChatComponent func_151521_b()
    {
        if (this.field_94556_a.size() == 0)
        {
            return new ChatComponentTranslation("death.attack.generic", new Object[] {this.fighter.func_145748_c_()});
        }
        else
        {
            CombatEntry combatentry = this.func_94544_f();
            CombatEntry combatentry1 = (CombatEntry)this.field_94556_a.get(this.field_94556_a.size() - 1);
            IChatComponent ichatcomponent = combatentry1.func_151522_h();
            Entity entity = combatentry1.getDamageSrc().getEntity();
            Object object;

            if (combatentry != null && combatentry1.getDamageSrc() == DamageSource.fall)
            {
                IChatComponent ichatcomponent1 = combatentry.func_151522_h();

                if (combatentry.getDamageSrc() != DamageSource.fall && combatentry.getDamageSrc() != DamageSource.outOfWorld)
                {
                    if (ichatcomponent1 != null && (ichatcomponent == null || !ichatcomponent1.equals(ichatcomponent)))
                    {
                        Entity entity1 = combatentry.getDamageSrc().getEntity();
                        ItemStack itemstack1 = entity1 instanceof EntityLivingBase ? ((EntityLivingBase)entity1).getHeldItem() : null;

                        if (itemstack1 != null && itemstack1.hasDisplayName())
                        {
                            object = new ChatComponentTranslation("death.fell.assist.item", new Object[] {this.fighter.func_145748_c_(), ichatcomponent1, itemstack1.func_151000_E()});
                        }
                        else
                        {
                            object = new ChatComponentTranslation("death.fell.assist", new Object[] {this.fighter.func_145748_c_(), ichatcomponent1});
                        }
                    }
                    else if (ichatcomponent != null)
                    {
                        ItemStack itemstack = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItem() : null;

                        if (itemstack != null && itemstack.hasDisplayName())
                        {
                            object = new ChatComponentTranslation("death.fell.finish.item", new Object[] {this.fighter.func_145748_c_(), ichatcomponent, itemstack.func_151000_E()});
                        }
                        else
                        {
                            object = new ChatComponentTranslation("death.fell.finish", new Object[] {this.fighter.func_145748_c_(), ichatcomponent});
                        }
                    }
                    else
                    {
                        object = new ChatComponentTranslation("death.fell.killer", new Object[] {this.fighter.func_145748_c_()});
                    }
                }
                else
                {
                    object = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(combatentry), new Object[] {this.fighter.func_145748_c_()});
                }
            }
            else
            {
                object = combatentry1.getDamageSrc().func_151519_b(this.fighter);
            }

            return (IChatComponent)object;
        }
    }

    public EntityLivingBase func_94550_c()
    {
        EntityLivingBase entitylivingbase = null;
        EntityPlayer entityplayer = null;
        float f = 0.0F;
        float f1 = 0.0F;
        Iterator iterator = this.field_94556_a.iterator();

        while (iterator.hasNext())
        {
            CombatEntry combatentry = (CombatEntry)iterator.next();

            if (combatentry.getDamageSrc().getEntity() instanceof EntityPlayer && (entityplayer == null || combatentry.func_94563_c() > f1))
            {
                f1 = combatentry.func_94563_c();
                entityplayer = (EntityPlayer)combatentry.getDamageSrc().getEntity();
            }

            if (combatentry.getDamageSrc().getEntity() instanceof EntityLivingBase && (entitylivingbase == null || combatentry.func_94563_c() > f))
            {
                f = combatentry.func_94563_c();
                entitylivingbase = (EntityLivingBase)combatentry.getDamageSrc().getEntity();
            }
        }

        if (entityplayer != null && f1 >= f / 3.0F)
        {
            return entityplayer;
        }
        else
        {
            return entitylivingbase;
        }
    }

    private CombatEntry func_94544_f()
    {
        CombatEntry combatentry = null;
        CombatEntry combatentry1 = null;
        byte b0 = 0;
        float f = 0.0F;

        for (int i = 0; i < this.field_94556_a.size(); ++i)
        {
            CombatEntry combatentry2 = (CombatEntry)this.field_94556_a.get(i);
            CombatEntry combatentry3 = i > 0 ? (CombatEntry)this.field_94556_a.get(i - 1) : null;

            if ((combatentry2.getDamageSrc() == DamageSource.fall || combatentry2.getDamageSrc() == DamageSource.outOfWorld) && combatentry2.func_94561_i() > 0.0F && (combatentry == null || combatentry2.func_94561_i() > f))
            {
                if (i > 0)
                {
                    combatentry = combatentry3;
                }
                else
                {
                    combatentry = combatentry2;
                }

                f = combatentry2.func_94561_i();
            }

            if (combatentry2.func_94562_g() != null && (combatentry1 == null || combatentry2.func_94563_c() > (float)b0))
            {
                combatentry1 = combatentry2;
            }
        }

        if (f > 5.0F && combatentry != null)
        {
            return combatentry;
        }
        else if (b0 > 5 && combatentry1 != null)
        {
            return combatentry1;
        }
        else
        {
            return null;
        }
    }

    private String func_94548_b(CombatEntry par1CombatEntry)
    {
        return par1CombatEntry.func_94562_g() == null ? "generic" : par1CombatEntry.func_94562_g();
    }

    private void func_94542_g()
    {
        this.field_94551_f = null;
    }

    private void func_94549_h()
    {
        int i = this.field_94552_d ? 300 : 100;

        if (this.field_94553_e && this.fighter.ticksExisted - this.field_94555_c > i)
        {
            this.field_94556_a.clear();
            this.field_94553_e = false;
            this.field_94552_d = false;
        }
    }
}