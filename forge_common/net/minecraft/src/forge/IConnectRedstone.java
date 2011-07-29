package net.minecraft.src.forge;
import net.minecraft.src.*;

public interface IConnectRedstone {
	public boolean canConnectRedstone(IBlockAccess iba, int i, int j, int k, int dir);
}
