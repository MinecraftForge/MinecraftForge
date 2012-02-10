/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

public class ArmorProperties 
{
    public double damageReduce = 0;
    public int    damageAbsorb = 0;
	
	public ArmorProperties () {}
    /**
      * Create an ArmorProperties describing the damage reduction.
      * 
      * @param absorb Damage absorption. Removed from damage before damage
      *    reduction computation is applied.
      * @param reduce Damage reduction, percentage of damage absorbed by
      *    armor where 1.0 =100%.  A full set of diamond armor is 80%.
      */
	public ArmorProperties (int absorb, double reduce) 
	{
	    damageReduce = reduce;
	    damageAbsorb = absorb;
	}
}
