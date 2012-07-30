package cpw.mods.fml.relauncher;

/**
 * The base plugin that provides class name meta information to FML to
 * enhance the classloading lifecycle for mods in FML
 *
 * @author cpw
 *
 */
public interface IFMLLoadingPlugin
{
    /**
     * Return a list of classes that implement the ILibrarySet interface
     *
     * @return
     */
    String[] getLibraryRequestClass();
    /**
     * Return a list of classes that implements the IClassTransformer interface
     * @return
     */
    String[] getASMTransformerClass();
}
