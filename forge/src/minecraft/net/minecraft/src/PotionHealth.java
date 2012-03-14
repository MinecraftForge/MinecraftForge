package net.minecraft.src;

public class PotionHealth extends Potion
{
    public PotionHealth(int par1, boolean par2, int par3)
    {
        super(par1, par2, par3);
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant()
    {
        return true;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(int par1, int par2)
    {
        return par1 >= 1;
    }
}
