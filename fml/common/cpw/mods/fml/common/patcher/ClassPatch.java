package cpw.mods.fml.common.patcher;

public class ClassPatch {
    public final String name;
    public final String sourceClassName;
    public final String targetClassName;
    public final boolean existsAtTarget;
    public final byte[] patch;
    public ClassPatch(String name, String sourceClassName, String targetClassName, boolean existsAtTarget, byte[] patch)
    {
        this.name = name;
        this.sourceClassName = sourceClassName;
        this.targetClassName = targetClassName;
        this.existsAtTarget = existsAtTarget;
        this.patch = patch;
    }
}
