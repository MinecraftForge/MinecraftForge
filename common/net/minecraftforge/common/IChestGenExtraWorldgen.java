package net.minecraftforge.common;

import java.util.Set;

import net.minecraft.world.World;

/**
 * @author monster860
 */

public interface IChestGenExtraWorldgen {
    public void generateExtra(int x, int y, int z, World w, Set<IChestGenExtraWorldgen> extraWorldgenSet);
    public boolean isBlockOccupied(int x, int y, int z, World w);
}
