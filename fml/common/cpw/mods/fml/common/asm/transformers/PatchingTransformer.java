package cpw.mods.fml.common.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import cpw.mods.fml.common.patcher.ClassPatchManager;

public class PatchingTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        return ClassPatchManager.INSTANCE.applyPatch(name, transformedName, bytes);
    }

}
