package cpw.mods.fml.common.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

public class TerminalTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        return basicClass;
    }

}
