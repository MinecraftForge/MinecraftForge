package net.minecraftforge.transformers;

import java.io.IOException;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class ForgeAccessTransformer extends AccessTransformer
{
    public ForgeAccessTransformer() throws IOException
    {
        super("forge_at.cfg");
    }
}
