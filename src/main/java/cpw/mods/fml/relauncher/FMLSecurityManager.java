package cpw.mods.fml.relauncher;

import java.security.Permission;

/**
 * A custom security manager stopping certain events from happening
 * unexpectedly.
 *
 * @author cpw
 *
 */
public class FMLSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm)
    {
        String permName = perm.getName() != null ? perm.getName() : "missing";
        if (permName.startsWith("exitVM"))
        {
            String callingClass = getClassContext()[4].getName();
            // FML is allowed to call system exit
            if (!callingClass.startsWith("cpw.mods.fml."))
            {
                throw new ExitTrappedException();
            }
        }
        else if ("setSecurityManager".equals(permName))
        {
            throw new SecurityException("Cannot replace the FML security manager");
        }
        return;
    }

    public static class ExitTrappedException extends SecurityException {
        private static final long serialVersionUID = 1L;
    }
}
