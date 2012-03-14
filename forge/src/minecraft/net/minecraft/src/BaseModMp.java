package net.minecraft.src;

public abstract class BaseModMp extends BaseMod
{
    public final int getId()
    {
        return this.toString().hashCode();
    }

    public void modsLoaded()
    {
        ModLoaderMp.initialize();
        super.modsLoaded();
    }

    public void handlePacket(Packet230ModLoader var1) {}

    public void handleTileEntityPacket(int var1, int var2, int var3, int var4, int[] var5, float[] var6, String[] var7) {}

    public GuiScreen handleGUI(int var1)
    {
        return null;
    }
}
