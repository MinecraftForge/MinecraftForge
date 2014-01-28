package net.minecraft.block;

import java.util.Random;
import net.minecraft.world.World;

public interface IGrowable
{
    boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5);

    boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5);

    void func_149853_b(World var1, Random var2, int var3, int var4, int var5);
}