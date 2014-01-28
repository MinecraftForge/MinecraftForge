package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IProgressUpdate
{
    // JAVADOC METHOD $$ func_73720_a
    void displayProgressMessage(String var1);

    // JAVADOC METHOD $$ func_73721_b
    @SideOnly(Side.CLIENT)
    void resetProgressAndMessage(String var1);

    // JAVADOC METHOD $$ func_73719_c
    void resetProgresAndWorkingMessage(String var1);

    // JAVADOC METHOD $$ func_73718_a
    void setLoadingProgress(int var1);

    @SideOnly(Side.CLIENT)
    void func_146586_a();
}