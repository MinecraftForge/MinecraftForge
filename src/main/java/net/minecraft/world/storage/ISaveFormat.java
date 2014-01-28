package net.minecraft.world.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat
{
    // JAVADOC METHOD $$ func_75804_a
    ISaveHandler getSaveLoader(String var1, boolean var2);

    @SideOnly(Side.CLIENT)
    List getSaveList() throws AnvilConverterException;

    void flushCache();

    // JAVADOC METHOD $$ func_75803_c
    @SideOnly(Side.CLIENT)
    WorldInfo getWorldInfo(String var1);

    // JAVADOC METHOD $$ func_75802_e
    boolean deleteWorldDirectory(String var1);

    // JAVADOC METHOD $$ func_75806_a
    @SideOnly(Side.CLIENT)
    void renameWorld(String var1, String var2);

    // JAVADOC METHOD $$ func_75801_b
    boolean isOldMapFormat(String var1);

    // JAVADOC METHOD $$ func_75805_a
    boolean convertMapFormat(String var1, IProgressUpdate var2);

    // JAVADOC METHOD $$ func_90033_f
    @SideOnly(Side.CLIENT)
    boolean canLoadWorld(String var1);
}