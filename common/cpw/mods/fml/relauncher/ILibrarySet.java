package cpw.mods.fml.relauncher;

/**
 * Interface for certain core plugins to register libraries to
 * be loaded in by the FML class loader at launch time
 *
 * @author cpw
 *
 */
public interface ILibrarySet
{
    /**
     * Return a list of libraries available from a common location
     *
     * @return a list of libraries available from a common location
     */
    String[] getLibraries();
    /**
     * Return the string encoded sha1 hash for each library in the returned list
     *
     * @return the string encoded sha1 hash for each library in the returned list
     */
    String[] getHashes();
    /**
     * Return the root URL format string from which this library set can be obtained
     * There needs to be a single %s string substitution which is the library name
     * @return the root URL format string from which this library set can be obtained
     */
    String getRootURL();
}
