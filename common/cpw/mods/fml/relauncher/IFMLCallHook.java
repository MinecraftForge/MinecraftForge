package cpw.mods.fml.relauncher;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * This call hook allows for code to execute at the very early stages of
 * minecraft initialization. FML uses it to validate that there is a
 * safe environment for further loading of FML.
 *
 * @author cpw
 *
 */
public interface IFMLCallHook extends Callable<Void>
{
    /**
     * Injected with data from the FML environment:
     * "classLoader" : The FML Class Loader
     * @param data
     */
    void injectData(Map<String,Object> data);
}
