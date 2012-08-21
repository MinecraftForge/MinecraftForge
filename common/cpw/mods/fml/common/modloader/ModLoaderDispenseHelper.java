package cpw.mods.fml.common.modloader;

import java.util.Random;

import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import cpw.mods.fml.common.IDispenseHandler;

public class ModLoaderDispenseHelper implements IDispenseHandler
{

    private BaseModProxy mod;

    public ModLoaderDispenseHelper(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public int dispense(double x, double y, double z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY,
            double entZ)
    {
        return mod.dispenseEntity(world, item, random, x, y, z, xVelocity, zVelocity, entX, entY, entZ);
    }

}
