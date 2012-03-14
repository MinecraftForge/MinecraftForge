package net.minecraft.src.flora;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.List;

public class FloraBoat extends Item
	implements ITextureProvider
{
    public FloraBoat(int i)
    {
        super(i);
        //maxStackSize = 1;
    }
    
    public int getMetadata(int md)
    {
        return md;
    }
    
    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(itemType[itemstack.getItemDamage()]).append("Boat").toString();
    }
    
    public static final String itemType[] =
    {
        "redwood", "blood", "white", "eucalyptus"
    };
    
    public int getIconFromDamage(int md)
    {
        return iconIndex + md;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        float f = 1.0F;
        float f1 = entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * f;
        float f2 = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f;
        double d = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double)f;
        double d1 = (entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * (double)f + 1.6200000000000001D) - (double)entityplayer.yOffset;
        double d2 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double)f;
        Vec3D vec3d = Vec3D.createVector(d, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
        float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
        float f5 = -MathHelper.cos(-f1 * 0.01745329F);
        float f6 = MathHelper.sin(-f1 * 0.01745329F);
        float f7 = f4 * f5;
        float f8 = f6;
        float f9 = f3 * f5;
        double d3 = 5D;
        Vec3D vec3d1 = vec3d.addVector((double)f7 * d3, (double)f8 * d3, (double)f9 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks_do(vec3d, vec3d1, true);
        if (movingobjectposition == null)
        {
            return itemstack;
        }
        Vec3D vec3d2 = entityplayer.getLook(f);
        boolean flag = false;
        float f10 = 1.0F;
        List list = world.getEntitiesWithinAABBExcludingEntity(entityplayer, entityplayer.boundingBox.addCoord(vec3d2.xCoord * d3, vec3d2.yCoord * d3, vec3d2.zCoord * d3).expand(f10, f10, f10));
        for (int l = 0; l < list.size(); l++)
        {
            Entity entity = (Entity)list.get(l);
            if (!entity.canBeCollidedWith())
            {
                continue;
            }
            float f11 = entity.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f11, f11, f11);
            if (axisalignedbb.isVecInside(vec3d))
            {
                flag = true;
            }
        }

        if (flag)
        {
            return itemstack;
        }
        if (movingobjectposition.typeOfHit == EnumMovingObjectType.TILE)
        {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if (!world.isRemote)
            {
                if (world.getBlockId(i, j, k) == Block.snow.blockID)
                {
                    j--;
                }
                switch(itemstack.getItemDamage())
                {
                case 0:	world.spawnEntityInWorld(new RedwoodBoat(world, (float)i + 0.5F, (float)j + 1.0F, (float)k + 0.5F));
                	break;
                case 1: world.spawnEntityInWorld(new BloodBoat(world, (float)i + 0.5F, (float)j + 1.0F, (float)k + 0.5F));
                	break;
                case 2: world.spawnEntityInWorld(new WhiteWoodBoat(world, (float)i + 0.5F, (float)j + 1.0F, (float)k + 0.5F));
                	break;
                case 3: world.spawnEntityInWorld(new EucalyptusBoat(world, (float)i + 0.5F, (float)j + 1.0F, (float)k + 0.5F));
            		break;
                default: return itemstack;
                }
            }
            if (!entityplayer.capabilities.depleteBuckets)
            {
                itemstack.stackSize--;
            }
        }
        return itemstack;
    }
    
    public String getTextureFile()
    {
        return "/floratex/infifood.png";
    }
}
