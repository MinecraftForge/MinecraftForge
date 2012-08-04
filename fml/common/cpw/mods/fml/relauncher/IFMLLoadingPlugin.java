package cpw.mods.fml.relauncher;

import java.util.Map;

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

    /**
     * Return a class name that implements "ModContainer" for injection into the mod list
     * The "getName" function should return a name that other mods can, if need be,
     * depend on.
     * Trivially, this modcontainer will be loaded before all regular mod containers,
     * which means it will be forced to be "immutable" - not susceptible to normal
     * sorting behaviour.
     * All other mod behaviours are available however- this container can receive and handle
     * normal loading events
     */
    String getModContainerClass();

    /**
     * Return the class name of an implementor of "IFMLCallHook", that will be run, in the
     * main thread, to perform any additional setup this coremod may require. It will be
     * run <strong>prior</strong> to Minecraft starting, so it CANNOT operate on minecraft
     * itself. The game will deliberately crash if this code is detected to trigger a
     * minecraft class loading (TODO: implement crash ;) )
     */
    String getSetupClass();

    /**
     * Inject coremod data into this coremod
     * This data includes:
     * "mcLocation" : the location of the minecraft directory,
     * "coremodList" : the list of coremods
     * "coremodLocation" : the file this coremod loaded from,
     */
    void injectData(Map<String, Object> data);
}
