package net.minecraft.src;
import java.util.*;

public class EntityCaveSpider extends EntitySpider
{
	Random rand;
	
    public EntityCaveSpider(World par1World)
    {
        super(par1World);
        this.texture = "/mob/cavespider.png";
        this.setSize(0.7F, 0.5F);
        rand = new Random();
    }

    public int getMaxHealth()
    {
        return 12;
    }

    /**
     * How large the spider should be scaled.
     */
    public float spiderScaleAmount()
    {
        return 0.7F;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        if (super.attackEntityAsMob(par1Entity))
        {
            if (par1Entity instanceof EntityLiving)
            {
                byte var2 = 0;

                if (this.worldObj.difficultySetting > 1)
                {
                    if (this.worldObj.difficultySetting == 2)
                    {
                        var2 = 7;
                    }
                    else if (this.worldObj.difficultySetting == 3)
                    {
                        var2 = 15;
                    }
                }

                if (var2 > 0 && rand.nextInt(rand.nextInt(15)) == 0)
                {
                    ((EntityLiving)par1Entity).addPotionEffect(new PotionEffect(Potion.poison.id, 
                    		var2 * rand.nextInt(6)+1 * 5, 0));
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
