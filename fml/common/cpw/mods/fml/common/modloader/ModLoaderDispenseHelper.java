package cpw.mods.fml.common.modloader;

import java.util.Random;

import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IDispenserHandler;

public class ModLoaderDispenseHelper implements IDispenserHandler
{

    private BaseModProxy mod;

    public ModLoaderDispenseHelper(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public int dispense(int x, int y, int z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY,
            double entZ)
    {
        int ret = mod.dispenseEntity(world, item, random, x, y, z, xVelocity, zVelocity, entX, entY, entZ);
        return ret == 0 ? -1 : ret;
    }

}
