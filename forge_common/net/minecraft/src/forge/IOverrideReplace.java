package net.minecraft.src.forge;
import net.minecraft.src.*;

public interface IOverrideReplace {
	public boolean canReplaceBlock(World world, int i, int j, int k, int bid);
	public boolean getReplacedSuccess();
}
