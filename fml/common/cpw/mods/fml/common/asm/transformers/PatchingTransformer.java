package cpw.mods.fml.common.asm.transformers;

import cpw.mods.fml.common.patcher.ClassPatchManager;
import cpw.mods.fml.relauncher.IClassTransformer;

public class PatchingTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        return ClassPatchManager.INSTANCE.applyPatch(name, transformedName, bytes);
    }

}
