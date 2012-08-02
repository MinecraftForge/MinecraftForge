package cpw.mods.fml.relauncher;

public interface IClassTransformer
{
    public byte[] transform(String name, byte[] bytes);
}
