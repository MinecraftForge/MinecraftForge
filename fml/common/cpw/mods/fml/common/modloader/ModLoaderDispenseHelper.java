package cpw.mods.fml.common.modloader;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
    	return -1;
    }

}
