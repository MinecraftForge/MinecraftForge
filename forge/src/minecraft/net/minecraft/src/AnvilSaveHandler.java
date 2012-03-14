package net.minecraft.src;

import java.io.File;
import java.util.List;

public class AnvilSaveHandler extends SaveHandler
{
    public AnvilSaveHandler(File par1File, String par2Str, boolean par3)
    {
        super(par1File, par2Str, par3);
    }

    /**
     * Returns the chunk loader with the provided world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider par1WorldProvider)
    {
        File var2 = this.getSaveDirectory();
        File var3;

        if (par1WorldProvider.getSaveFolder() != null)
        {
            var3 = new File(var2, par1WorldProvider.getSaveFolder());
            var3.mkdirs();
            return new AnvilChunkLoader(var3);
        }
        else
        {
            return new AnvilChunkLoader(var2);
        }
    }

    public void saveWorldInfoAndPlayer(WorldInfo par1WorldInfo, List par2List)
    {
        par1WorldInfo.setSaveVersion(19133);
        super.saveWorldInfoAndPlayer(par1WorldInfo, par2List);
    }
}
