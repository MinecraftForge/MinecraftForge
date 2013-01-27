package cpw.mods.fml.relauncher;

public interface IClassNameTransformer {
    public String remapClassName(String name);

    public String unmapClassName(String name);
}
