package net.minecraft.src.balkon;

import net.minecraft.src.*;

public abstract class BalkPhysHelper
{
    public BalkPhysHelper()
    {
    }

    public static BalkExplosion createAdvancedExplosion(World world, Entity entity, double d, double d1, double d2,
            float f, boolean flag, boolean flag1, boolean flag2, boolean flag3)
    {
        BalkExplosion balkexplosion = new BalkExplosion(world, entity, d, d1, d2, f);
        balkexplosion.isFlaming = flag;
        balkexplosion.doExplosionA();
        balkexplosion.doExplosionB(flag2, flag3, flag1);
        return balkexplosion;
    }

    public static BalkExplosion createAdvancedExplosion(World world, Entity entity, double d, double d1, double d2,
            float f, boolean flag, boolean flag1, boolean flag2)
    {
        BalkExplosion balkexplosion = new BalkExplosion(world, entity, d, d1, d2, f);
        balkexplosion.isFlaming = flag;
        balkexplosion.doExplosionA();
        balkexplosion.doExplosionB(flag2, flag2, flag1);
        return balkexplosion;
    }

    public static void knockBack(EntityLiving entityliving, EntityLiving entityliving1, float f)
    {
        entityliving.motionX = 0.0D;
        entityliving.motionY = 0.0D;
        entityliving.motionZ = 0.0D;
        float f1 = 2.5F;
        if ((entityliving1 instanceof EntityPlayer) && entityliving1.isSprinting())
        {
            entityliving.addVelocity(-Math.sin(Math.toRadians(entityliving1.rotationYaw)) * (double)f * (double)f1, 0.1F * f * f1, Math.cos(Math.toRadians(entityliving1.rotationYaw)) * (double)f * (double)f1);
        }
        double d = entityliving1.posX - entityliving.posX;
        double d1;
        for (d1 = entityliving1.posZ - entityliving.posZ; d * d + d1 * d1 < 0.0001D; d1 = (Math.random() - Math.random()) * 0.01D)
        {
            d = (Math.random() - Math.random()) * 0.01D;
        }

        entityliving.attackedAtYaw = (float)((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - entityliving.rotationYaw;
        float f2 = MathHelper.sqrt_double(d * d + d1 * d1);
        float f3 = f;
        entityliving.motionX -= (d / (double)f2) * (double)f3;
        entityliving.motionY += 0.40000000000000002D;
        entityliving.motionZ -= (d1 / (double)f2) * (double)f3;
        if (entityliving.motionY > 0.40000000000000002D)
        {
            entityliving.motionY = 0.40000000000000002D;
        }
    }
}
