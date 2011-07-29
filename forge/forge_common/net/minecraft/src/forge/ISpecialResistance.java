package net.minecraft.src.forge;
import net.minecraft.src.*;

public interface ISpecialResistance {
	public float getSpecialExplosionResistance(World world, int i, int j, int k,
		double src_x, double src_y, double src_z, Entity exploder);
}
