package net.minecraftforge.common;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * A universal API for heat providing blocks, as per http://www.minecraftforge.net/forum/index.php?topic=21567.0
 *
 * This could (should) be used to allow blocks that need heat sources, such as Witchery's Kettle, or Thaumcraft's Crucible,
 * to check for mod heat providing blocks.
 *
 * @author warlordjones
 *
 */
public interface IHeatProvider {
    /**
     *  @return strength of heat provided.
     */
    public int getHeatStrength();
    /**
     *  @return direction heat is being provided in. Use UNKNOWN for all directions.
     */
    public ForgeDirection getHeatDirection();
}
